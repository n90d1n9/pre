package tech.kayys.syirkah.billing.application.api.query;

import tech.kayys.syirkah.foundation.domain.valueobject.Money;
import java.time.Instant;

public record BillingStatistics(
        Instant fromDate,
        Instant toDate,
        long totalActiveSchedules,
        Money totalDueAmount,
        Money totalCollectedAmount,
        int totalFailedPayments,
        double successRate,
        Money averageRevenue
) {}
