package tech.kayys.syirkah.crm.domain.repository;

import tech.kayys.syirkah.crm.domain.identifier.CustomerId;
import tech.kayys.syirkah.crm.domain.identifier.OpportunityId;
import tech.kayys.syirkah.crm.domain.model.Opportunity;
import tech.kayys.syirkah.crm.domain.valueobject.OpportunityStage;
import tech.kayys.syirkah.foundation.domain.repository.Repository;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface OpportunityRepository extends Repository<Opportunity, OpportunityId> {
    CompletionStage<List<Opportunity>> findByCustomerId(CustomerId customerId);
    CompletionStage<List<Opportunity>> findByStage(OpportunityStage stage);
    CompletionStage<List<Opportunity>> findActiveOpportunities();
}
