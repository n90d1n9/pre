package tech.kayys.syirkah.billing.application.api.query;

import tech.kayys.syirkah.foundation.application.query.Query;
import java.time.Instant;

public record BillingStatisticsQuery(
        Instant fromDate,
        Instant toDate
) implements Query {}
