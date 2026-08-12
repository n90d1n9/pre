package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.CreateOpportunityCommand;
import tech.kayys.erp.crm.application.port.CustomerPort;
import tech.kayys.erp.crm.domain.identifier.CustomerId;
import tech.kayys.erp.crm.domain.identifier.OpportunityId;
import tech.kayys.erp.crm.domain.model.Opportunity;
import tech.kayys.erp.crm.domain.repository.OpportunityRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating opportunities.
 */
@UseCase("Create a new opportunity")
public class CreateOpportunityHandler implements CommandHandler<CreateOpportunityCommand, OpportunityId> {

    private final OpportunityRepository opportunityRepository;
    private final CustomerPort customerPort;

    @Inject
    public CreateOpportunityHandler(OpportunityRepository opportunityRepository, CustomerPort customerPort) {
        this.opportunityRepository = opportunityRepository;
        this.customerPort = customerPort;
    }

    @Override
    public CompletionStage<OpportunityId> handle(CreateOpportunityCommand command) {
        // Validate customer exists
        return customerPort.validateCustomer(command.customerId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer not found: " + command.customerId())
                    );
                }

                // Create the opportunity
                Opportunity opportunity = Opportunity.create(
                    command.opportunityId(),
                    command.name(),
                    CustomerId.of(command.customerId()),
                    command.customerName() != null ? command.customerName() : "Unknown Customer",
                    command.estimatedValue(),
                    command.currencyCode()
                );

                // Set optional fields
                if (command.description() != null) {
                    opportunity.setDescription(command.description());
                }
                if (command.stage() != null) {
                    opportunity.moveStage(command.stage());
                }
                if (command.assignedTo() != null) {
                    opportunity.assign(command.assignedTo());
                }
                if (command.expectedCloseDate() != null) {
                    opportunity.setExpectedCloseDate(command.expectedCloseDate());
                }
                if (command.leadSource() != null) {
                    opportunity.setLeadSource(command.leadSource());
                }
                if (command.productInterest() != null) {
                    opportunity.setProductInterest(command.productInterest());
                }
                if (command.notes() != null) {
                    opportunity.setNotes(command.notes());
                }

                // Save the opportunity
                return opportunityRepository.save(opportunity)
                    .thenApply(Opportunity::getId);
            });
    }
}