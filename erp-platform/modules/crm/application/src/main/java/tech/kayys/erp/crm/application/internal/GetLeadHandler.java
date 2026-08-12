package tech.kayys.erp.crm.application.internal;

import tech.kayys.erp.foundation.application.QueryHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.crm.application.api.query.GetLeadQuery;
import tech.kayys.erp.crm.application.api.query.LeadView;
import tech.kayys.erp.crm.domain.repository.LeadRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for getting a lead.
 */
@UseCase("Get a lead by ID")
public class GetLeadHandler implements QueryHandler<GetLeadQuery, LeadView> {

    private final LeadRepository leadRepository;

    @Inject
    public GetLeadHandler(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Override
    public CompletionStage<LeadView> handle(GetLeadQuery query) {
        return leadRepository.findById(query.leadId())
            .thenApply(leadOpt -> leadOpt
                .map(LeadView::fromDomain)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found"))
            );
    }
}