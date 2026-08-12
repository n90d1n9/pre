package tech.kayys.erp.subscription.application.api;

import tech.kayys.erp.subscription.application.api.command.ActivateSubscriptionCommand;
import tech.kayys.erp.subscription.application.api.command.CancelSubscriptionCommand;
import tech.kayys.erp.subscription.application.api.command.CreateSubscriptionCommand;
import tech.kayys.erp.subscription.application.api.command.PauseSubscriptionCommand;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for subscription commands.
 */
public interface SubscriptionCommandService {

    /**
     * Creates a new subscription.
     */
    CompletionStage<SubscriptionId> createSubscription(CreateSubscriptionCommand command);

    /**
     * Activates a subscription.
     */
    CompletionStage<SubscriptionId> activateSubscription(ActivateSubscriptionCommand command);

    /**
     * Pauses a subscription.
     */
    CompletionStage<SubscriptionId> pauseSubscription(PauseSubscriptionCommand command);

    /**
     * Cancels a subscription.
     */
    CompletionStage<SubscriptionId> cancelSubscription(CancelSubscriptionCommand command);

    /**
     * Processes renewals for all due subscriptions.
     */
    CompletionStage<Integer> processRenewals();
}