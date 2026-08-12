package tech.kayys.erp.subscription.application.api;

import tech.kayys.erp.subscription.application.api.query.GetSubscriptionQuery;
import tech.kayys.erp.subscription.application.api.query.SubscriptionView;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for subscription queries.
 */
public interface SubscriptionQueryService {

    /**
     * Gets a subscription by ID.
     */
    CompletionStage<SubscriptionView> getSubscription(GetSubscriptionQuery query);

    /**
     * Gets all subscriptions for a customer.
     */
    CompletionStage<List<SubscriptionView>> getCustomerSubscriptions(CustomerId customerId);

    /**
     * Gets active subscriptions for a customer.
     */
    default CompletionStage<List<SubscriptionView>> getActiveCustomerSubscriptions(
            CustomerId customerId) {
        return getCustomerSubscriptions(customerId)
            .thenApply(subscriptions -> subscriptions.stream()
                .filter(SubscriptionView::isActive)
                .toList()
            );
    }

    /**
     * Checks if a customer has an active subscription.
     */
    default CompletionStage<Boolean> customerHasActiveSubscription(CustomerId customerId) {
        return getActiveCustomerSubscriptions(customerId)
            .thenApply(subscriptions -> !subscriptions.isEmpty());
    }

    /**
     * Gets subscriptions needing renewal.
     */
    CompletionStage<List<SubscriptionView>> getSubscriptionsNeedingRenewal();
}