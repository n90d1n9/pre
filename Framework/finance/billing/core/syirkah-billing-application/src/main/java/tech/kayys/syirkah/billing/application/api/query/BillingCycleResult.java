package tech.kayys.syirkah.billing.application.api.query;

import tech.kayys.syirkah.billing.domain.identifier.BillingScheduleId;
import tech.kayys.syirkah.foundation.domain.valueobject.Money;
import java.time.Instant;

public record BillingCycleResult(
        BillingScheduleId scheduleId,
        int cycleNumber,
        boolean success,
        Money amount,
        String invoiceId,
        String transactionId,
        String message,
        Instant processedAt
) {}
