package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

/**
 * Command to pause a subscription.
 */
public record PauseSubscriptionCommand(
        SubscriptionId subscriptionId
) implements Command<SubscriptionId> {

    public PauseSubscriptionCommand {
        if (subscriptionId == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }
    }
}