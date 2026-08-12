package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Command to cancel a subscription.
 */
public record CancelSubscriptionCommand(
        SubscriptionId subscriptionId,
        String reason
) implements Command<SubscriptionId> {

    public CancelSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }
}