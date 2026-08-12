package tech.kayys.erp.subscription.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Query to get a subscription by ID.
 */
public record GetSubscriptionQuery(
        SubscriptionId subscriptionId
) implements Query<SubscriptionView> {

    public GetSubscriptionQuery {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}