package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.command.CreateLeadCommand;
import tech.kayys.erp.crm.domain.identifier.LeadId;
import tech.kayys.erp.crm.domain.model.Lead;
import tech.kayys.erp.crm.domain.repository.LeadRepository;
import tech.kayys.erp.crm.application.port.LeadScoringPort;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating leads.
 */
@UseCase("Create a new lead")
public class CreateLeadHandler implements CommandHandler<CreateLeadCommand, LeadId> {

    private final LeadRepository leadRepository;
    private final LeadScoringPort leadScoringPort;

    @Inject
    public CreateLeadHandler(LeadRepository leadRepository, LeadScoringPort leadScoringPort) {
        this.leadRepository = leadRepository;
        this.leadScoringPort = leadScoringPort;
    }

    @Override
    public CompletionStage<LeadId> handle(CreateLeadCommand command) {
        // Check if lead already exists by email
        return leadRepository.findByEmail(command.email())
            .thenCompose(existingLeads -> {
                if (!existingLeads.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Lead with email already exists: " + command.email())
                    );
                }

                // Create the lead
                Lead lead = Lead.create(
                    command.leadId(),
                    command.firstName(),
                    command.lastName(),
                    command.email(),
                    command.source()
                );

                // Set optional fields
                if (command.phone() != null) {
                    lead.setPhone(command.phone());
                }
                if (command.company() != null) {
                    lead.setCompany(command.company());
                }
                if (command.jobTitle() != null) {
                    lead.setJobTitle(command.jobTitle());
                }
                if (command.industry() != null) {
                    lead.setIndustry(command.industry());
                }
                if (command.notes() != null) {
                    lead.setNotes(command.notes());
                }

                // Calculate lead score
                return leadScoringPort.calculateScore(lead)
                    .thenApply(score -> {
                        lead.updateScore(score);
                        return lead;
                    })
                    .thenCompose(leadRepository::save)
                    .thenApply(Lead::getId);
            });
    }
}