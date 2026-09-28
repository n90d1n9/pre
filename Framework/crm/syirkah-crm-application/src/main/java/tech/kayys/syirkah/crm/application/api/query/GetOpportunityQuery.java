package tech.kayys.syirkah.crm.application.api.query;

import tech.kayys.syirkah.crm.domain.identifier.OpportunityId;
import tech.kayys.syirkah.foundation.application.query.Query;

public record GetOpportunityQuery(OpportunityId opportunityId) implements Query {}
