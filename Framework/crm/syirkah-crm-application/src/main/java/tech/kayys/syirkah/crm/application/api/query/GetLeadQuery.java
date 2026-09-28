package tech.kayys.syirkah.crm.application.api.query;

import tech.kayys.syirkah.foundation.application.query.Query;
import tech.kayys.syirkah.crm.domain.identifier.LeadId;

/**
 * Query to get a lead by ID.
 */
public record GetLeadQuery(LeadId leadId) implements Query {

    public GetLeadQuery {
        if (leadId == null) {
            throw new IllegalArgumentException("Lead ID cannot be null");
        }
    }
}