package tech.kayys.syirkah.crm.application.opportunity;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.crm.application.api.command.CreateOpportunityCommand;
import tech.kayys.syirkah.crm.domain.identifier.CustomerId;
import tech.kayys.syirkah.crm.domain.identifier.OpportunityId;
import tech.kayys.syirkah.crm.domain.model.Opportunity;
import tech.kayys.syirkah.crm.domain.repository.OpportunityRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;

import java.util.Objects;

/**
 * Creates an {@link Opportunity}. Stage is applied through the
 * aggregate's {@code moveStage} so probability and weighted value stay
 * consistent with the stage.
 */
public final class CreateOpportunityHandler implements CommandHandler<CreateOpportunityCommand, OpportunityId> {

    private final OpportunityRepository opportunityRepository;

    public CreateOpportunityHandler(OpportunityRepository opportunityRepository) {
        this.opportunityRepository = Objects.requireNonNull(opportunityRepository, "opportunityRepository cannot be null");
    }

    @Override
    public Uni<OpportunityId> handle(CreateOpportunityCommand command) {
        Objects.requireNonNull(command, "command cannot be null");

        Opportunity opportunity = Opportunity.create(
                command.opportunityId(),
                command.name(),
                CustomerId.of(command.customerId()),
                command.customerName(),
                command.estimatedValue(),
                command.currencyCode());
        opportunity.setDescription(command.description());
        if (command.stage() != null) {
            opportunity.moveStage(command.stage());
        }
        if (command.assignedTo() != null) {
            opportunity.assign(command.assignedTo());
        }
        if (command.expectedCloseDate() != null) {
            opportunity.setExpectedCloseDate(command.expectedCloseDate());
        }
        opportunity.setLeadSource(command.leadSource());
        opportunity.setProductInterest(command.productInterest());
        opportunity.setNotes(command.notes());

        return Uni.createFrom().completionStage(opportunityRepository.save(opportunity))
                .replaceWith(opportunity.getId());
    }
}
