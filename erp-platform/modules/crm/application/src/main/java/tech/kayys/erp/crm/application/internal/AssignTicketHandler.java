package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.AssignTicketCommand;
import tech.kayys.erp.crm.application.port.NotificationPort;
import tech.kayys.erp.crm.domain.identifier.TicketId;
import tech.kayys.erp.crm.domain.repository.SupportTicketRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for assigning tickets.
 */
@UseCase("Assign a ticket to an agent")
public class AssignTicketHandler implements CommandHandler<AssignTicketCommand, TicketId> {

    private final SupportTicketRepository ticketRepository;
    private final NotificationPort notificationPort;

    @Inject
    public AssignTicketHandler(SupportTicketRepository ticketRepository, NotificationPort notificationPort) {
        this.ticketRepository = ticketRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public CompletionStage<TicketId> handle(AssignTicketCommand command) {
        return ticketRepository.findById(command.ticketId())
            .thenCompose(ticketOpt -> {
                if (ticketOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Ticket not found: " + command.ticketId())
                    );
                }

                SupportTicket ticket = ticketOpt.get();

                // Check if ticket can be assigned
                if (ticket.getStatus() == TicketStatus.CLOSED) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Cannot assign closed ticket")
                    );
                }

                // Assign the ticket
                ticket.assign(command.assignedTo());

                // Set SLA expectations based on priority
                ticket.setSLAExpectations(ticket.getPriority());

                return ticketRepository.save(ticket)
                    .thenCompose(saved -> {
                        // Send notification to assigned agent
                        return notificationPort.sendTicketAssignedNotification(saved, command.assignedTo())
                            .thenApply(v -> saved.getId());
                    });
            });
    }
}