package tech.kayys.erp.subscription.application.api.query;

import tech.kayys.erp.subscription.domain.model.Subscription;

import java.time.Instant;

/**
 * View of a subscription.
 */
public record SubscriptionView(
        String subscriptionId,
        String customerId,
        String planId,
        String status,
        String billingCycle,
        String monthlyFee,
        String currency,
        String startDate,
        String nextBillingDate,
        String endDate,
        boolean isActive,
        boolean isInTrial,
        boolean autoRenew,
        String cancellationReason,
        String totalPaid
) {

    public static SubscriptionView fromDomain(Subscription subscription) {
        return new SubscriptionView(
            subscription.getId().toString(),
            subscription.getCustomerId().toString(),
            subscription.getPlanId().toString(),
            subscription.getStatus().name(),
            subscription.getBillingCycle().name(),
            subscription.getMonthlyFee().getAmount().toPlainString(),
            subscription.getMonthlyFee().getCurrency().getCurrencyCode(),
            subscription.getStartDate().toString(),
            subscription.getNextBillingDate().toString(),
            subscription.getEndDate() != null ? subscription.getEndDate().toString() : null,
            subscription.isActive(),
            subscription.isInTrial(),
            subscription.isAutoRenew(),
            subscription.getCancellationReason(),
            subscription.getTotalPaid().getAmount().toPlainString()
        );
    }
}