package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.ResolveTicketCommand;
import tech.kayys.erp.crm.application.port.NotificationPort;
import tech.kayys.erp.crm.domain.identifier.TicketId;
import tech.kayys.erp.crm.domain.repository.SupportTicketRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for resolving tickets.
 */
@UseCase("Resolve a ticket")
public class ResolveTicketHandler implements CommandHandler<ResolveTicketCommand, TicketId> {

    private final SupportTicketRepository ticketRepository;
    private final NotificationPort notificationPort;

    @Inject
    public ResolveTicketHandler(SupportTicketRepository ticketRepository, NotificationPort notificationPort) {
        this.ticketRepository = ticketRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public CompletionStage<TicketId> handle(ResolveTicketCommand command) {
        return ticketRepository.findById(command.ticketId())
            .thenCompose(ticketOpt -> {
                if (ticketOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Ticket not found: " + command.ticketId())
                    );
                }

                SupportTicket ticket = ticketOpt.get();

                // Check if ticket can be resolved
                if (ticket.getStatus() == TicketStatus.CLOSED) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Cannot resolve closed ticket")
                    );
                }

                if (ticket.getStatus() != TicketStatus.IN_PROGRESS && 
                    ticket.getStatus() != TicketStatus.PENDING_CUSTOMER &&
                    ticket.getStatus() != TicketStatus.ASSIGNED) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Cannot resolve ticket in status: " + ticket.getStatus())
                    );
                }

                // Resolve the ticket
                ticket.resolve(command.resolution());
                ticket.calculateTimeToResolution();

                return ticketRepository.save(ticket)
                    .thenCompose(saved -> {
                        // Send notification to customer
                        return notificationPort.sendTicketResolvedNotification(saved)
                            .thenApply(v -> saved.getId());
                    });
            });
    }
}