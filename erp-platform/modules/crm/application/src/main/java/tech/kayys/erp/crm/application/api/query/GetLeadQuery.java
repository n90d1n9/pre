package tech.kayys.erp.crm.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.crm.domain.identifier.LeadId;

/**
 * Query to get a lead by ID.
 */
public record GetLeadQuery(LeadId leadId) implements Query<LeadView> {

    public GetLeadQuery {
        if (leadId == null) {
            throw new IllegalArgumentException("Lead ID cannot be null");
        }
    }
}