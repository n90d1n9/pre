package tech.kayys.syirkah.billing.application.api.query;

import tech.kayys.syirkah.foundation.application.query.Query;

public record UpcomingBillingsQuery(
        int daysAhead
) implements Query {}
