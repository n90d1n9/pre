package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.MoveOpportunityStageCommand;
import tech.kayys.erp.crm.application.port.NotificationPort;
import tech.kayys.erp.crm.domain.identifier.OpportunityId;
import tech.kayys.erp.crm.domain.repository.OpportunityRepository;
import tech.kayys.erp.crm.domain.valueobject.OpportunityStage;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for moving opportunity stages.
 */
@UseCase("Move an opportunity to a new stage")
public class MoveOpportunityStageHandler implements CommandHandler<MoveOpportunityStageCommand, OpportunityId> {

    private final OpportunityRepository opportunityRepository;
    private final NotificationPort notificationPort;

    @Inject
    public MoveOpportunityStageHandler(OpportunityRepository opportunityRepository, NotificationPort notificationPort) {
        this.opportunityRepository = opportunityRepository;
        this.notificationPort = notificationPort;
    }

    @Override
    public CompletionStage<OpportunityId> handle(MoveOpportunityStageCommand command) {
        return opportunityRepository.findById(command.opportunityId())
            .thenCompose(opportunityOpt -> {
                if (opportunityOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Opportunity not found: " + command.opportunityId())
                    );
                }

                Opportunity opportunity = opportunityOpt.get();

                // Check if stage transition is valid
                if (!opportunity.getStage().canTransitionTo(command.newStage())) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException(
                            "Cannot transition from " + opportunity.getStage() + 
                            " to " + command.newStage()
                        )
                    );
                }

                // Move to new stage
                opportunity.moveToStage(command.newStage());

                // Add reason as note if provided
                if (command.reason() != null) {
                    opportunity.setNotes(command.reason());
                }

                // Send notification for important stage changes
                boolean isWin = command.newStage() == OpportunityStage.WON;
                boolean isLoss = command.newStage() == OpportunityStage.LOST;
                boolean isClosing = command.newStage() == OpportunityStage.CLOSING;

                return opportunityRepository.save(opportunity)
                    .thenCompose(saved -> {
                        if (isWin) {
                            return notificationPort.sendOpportunityWonNotification(saved)
                                .thenApply(v -> saved.getId());
                        } else if (isLoss) {
                            return notificationPort.sendOpportunityLostNotification(saved)
                                .thenApply(v -> saved.getId());
                        } else if (isClosing) {
                            return notificationPort.sendOpportunityClosingNotification(saved)
                                .thenApply(v -> saved.getId());
                        }
                        return CompletableFuture.completedStage(saved.getId());
                    });
            });
    }
}