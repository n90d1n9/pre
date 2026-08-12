package tech.kayys.erp.subscription.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.subscription.application.api.command.CreateSubscriptionCommand;
import tech.kayys.erp.subscription.application.port.PricingProviderPort;
import tech.kayys.erp.subscription.application.port.CustomerValidationPort;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.model.SubscriptionPlan;
import tech.kayys.erp.subscription.domain.repository.SubscriptionPlanRepository;
import tech.kayys.erp.subscription.domain.repository.SubscriptionRepository;
import tech.kayys.erp.subscription.domain.valueobject.Money;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating subscriptions.
 */
@UseCase("Create a new subscription")
public class CreateSubscriptionHandler 
        implements CommandHandler<CreateSubscriptionCommand, SubscriptionId> {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final CustomerValidationPort customerValidationPort;
    private final PricingProviderPort pricingProviderPort;

    @Inject
    public CreateSubscriptionHandler(
            SubscriptionRepository subscriptionRepository,
            SubscriptionPlanRepository planRepository,
            CustomerValidationPort customerValidationPort,
            PricingProviderPort pricingProviderPort) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.customerValidationPort = customerValidationPort;
        this.pricingProviderPort = pricingProviderPort;
    }

    @Override
    public CompletionStage<SubscriptionId> handle(CreateSubscriptionCommand command) {
        // 1. Validate customer exists
        return customerValidationPort.validateCustomer(command.customerId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Customer not found: " + command.customerId())
                    );
                }

                // 2. Get the plan
                return planRepository.findById(PlanId.of(command.planId()))
                    .thenCompose(planOpt -> {
                        if (planOpt.isEmpty()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Plan not found: " + command.planId())
                            );
                        }

                        SubscriptionPlan plan = planOpt.get();

                        // 3. Validate plan is active
                        if (!plan.isActive()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Plan is not active")
                            );
                        }

                        // 4. Get pricing for the plan
                        Money monthlyFee = plan.getPriceForCycle(command.billingCycle());

                        // 5. Create the subscription
                        Subscription subscription = Subscription.create(
                            command.subscriptionId(),
                            CustomerId.of(command.customerId()),
                            PlanId.of(command.planId()),
                            monthlyFee,
                            command.billingCycle()
                        );

                        // 6. Set auto-renew
                        subscription.setAutoRenew(command.autoRenew());

                        // 7. Set trial period if specified
                        if (command.trialDays() != null && command.trialDays() > 0) {
                            subscription.setTrialPeriod(command.trialDays());
                        }

                        // 8. Save the subscription
                        return subscriptionRepository.save(subscription)
                            .thenApply(Subscription::getId);
                    });
            });
    }
}