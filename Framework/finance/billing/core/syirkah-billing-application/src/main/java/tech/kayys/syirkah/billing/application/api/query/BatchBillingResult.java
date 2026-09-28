package tech.kayys.syirkah.billing.application.api.query;

import tech.kayys.syirkah.foundation.domain.valueobject.Money;

public record BatchBillingResult(
        int totalProcessed,
        int successful,
        int failed,
        int resultsCount,
        Money totalAmount,
        String message
) {}
