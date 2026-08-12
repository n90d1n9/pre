package tech.kayys.erp.subscription.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Subscription aggregates.
 */
public interface SubscriptionRepository extends Repository<Subscription, SubscriptionId> {

    /**
     * Finds all subscriptions for a customer.
     */
    CompletionStage<List<Subscription>> findByCustomerId(CustomerId customerId);

    /**
     * Finds active subscriptions for a customer.
     */
    default CompletionStage<List<Subscription>> findActiveByCustomerId(CustomerId customerId) {
        return findByCustomerId(customerId)
            .thenApply(subscriptions -> subscriptions.stream()
                .filter(Subscription::isActive)
                .toList()
            );
    }

    /**
     * Finds subscriptions by status.
     */
    CompletionStage<List<Subscription>> findByStatus(SubscriptionStatus status);

    /**
     * Finds subscriptions that need renewal (nextBillingDate < now).
     */
    CompletionStage<List<Subscription>> findSubscriptionsNeedingRenewal();

    /**
     * Finds subscriptions for a specific plan.
     */
    CompletionStage<List<Subscription>> findByPlanId(PlanId planId);

    /**
     * Finds subscriptions expiring between two dates.
     */
    CompletionStage<List<Subscription>> findExpiringBetween(Instant start, Instant end);

    /**
     * Counts subscriptions by status.
     */
    CompletionStage<Long> countByStatus(SubscriptionStatus status);

    /**
     * Counts active subscriptions for a customer.
     */
    default CompletionStage<Long> countActiveByCustomerId(CustomerId customerId) {
        return findActiveByCustomerId(customerId)
            .thenApply(List::size)
            .thenApply(Long::valueOf);
    }
}