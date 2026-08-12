package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.CreateTicketCommand;
import tech.kayys.erp.crm.application.port.CustomerPort;
import tech.kayys.erp.crm.application.port.NotificationPort;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.identifier.TicketId;
import tech.kayys.erp.crm.domain.model.SupportTicket;
import tech.kayys.erp.crm.domain.repository.SupportTicketRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating support tickets.
 */
@UseCase("Create a support ticket")
public class CreateTicketHandler implements CommandHandler<CreateTicketCommand, TicketId> {

    private final SupportTicketRepository ticketRepository;
    private final CustomerPort customerPort;
    private final NotificationPort notificationPort;

    @Inject
    public CreateTicketHandler(
            SupportTicketRepository ticketRepository,
            CustomerPort customerPort,
            NotificationPort notificationPort) {
        this.ticketRepository = ticketRepository;
        this.customerPort = customerPort;
        this.notificationPort = notificationPort;
    }

    @Override
    public CompletionStage<TicketId> handle(CreateTicketCommand command) {
        // 1. Validate customer exists
        return customerPort.validateCustomer(command.customerId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer not found: " + command.customerId())
                    );
                }

                // 2. Generate ticket number
                return ticketRepository.generateTicketNumber()
                    .thenApply(ticketNumber -> {
                        // 3. Create ticket
                        return SupportTicket.create(
                            command.ticketId(),
                            ticketNumber,
                            CustomerId.of(command.customerId()),
                            command.customerName(),
                            command.subject(),
                            command.description(),
                            command.priority(),
                            command.category()
                        );
                    })
                    .thenCompose(ticket -> 
                        // 4. Save ticket
                        ticketRepository.save(ticket)
                    )
                    .thenCompose(savedTicket -> {
                        // 5. Send notification
                        return notificationPort.sendTicketCreatedNotification(savedTicket)
                            .thenApply(v -> savedTicket.getId());
                    });
            });
    }
}