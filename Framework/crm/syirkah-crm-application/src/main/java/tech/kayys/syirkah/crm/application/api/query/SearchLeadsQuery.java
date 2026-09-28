package tech.kayys.syirkah.crm.application.api.query;

import tech.kayys.syirkah.crm.domain.valueobject.LeadStatus;
import tech.kayys.syirkah.foundation.application.query.Query;

public record SearchLeadsQuery(
        LeadStatus status,
        String email,
        String company,
        String source,
        Double minScore,
        int page,
        int size
) implements Query {}
