package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Command to activate a subscription.
 */
public record ActivateSubscriptionCommand(
        SubscriptionId subscriptionId
) implements Command<SubscriptionId> {

    public ActivateSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}