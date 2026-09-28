package tech.kayys.syirkah.crm.application.api.query;

import tech.kayys.syirkah.foundation.application.query.Query;

public record SearchCustomersQuery(
        String companyName,
        String email,
        String industry,
        String city,
        String country,
        int page,
        int size
) implements Query {}
