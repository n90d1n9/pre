package tech.kayys.erp.subscription.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.valueobject.BillingCycle;

import java.util.UUID;

/**
 * Command to create a new subscription.
 */
public record CreateSubscriptionCommand(
        SubscriptionId subscriptionId,
        UUID customerId,
        UUID planId,
        BillingCycle billingCycle,
        Boolean autoRenew,
        Integer trialDays
) implements Command<SubscriptionId> {

    public CreateSubscriptionCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (planId == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }
        if (billingCycle == null) {
            throw new IllegalArgumentException("Billing cycle is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SubscriptionId subscriptionId;
        private UUID customerId;
        private UUID planId;
        private BillingCycle billingCycle = BillingCycle.MONTHLY;
        private Boolean autoRenew = true;
        private Integer trialDays;

        public Builder subscriptionId(SubscriptionId subscriptionId) {
            this.subscriptionId = subscriptionId;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder planId(UUID planId) {
            this.planId = planId;
            return this;
        }

        public Builder billingCycle(BillingCycle billingCycle) {
            this.billingCycle = billingCycle;
            return this;
        }

        public Builder autoRenew(Boolean autoRenew) {
            this.autoRenew = autoRenew;
            return this;
        }

        public Builder trialDays(Integer trialDays) {
            this.trialDays = trialDays;
            return this;
        }

        public CreateSubscriptionCommand build() {
            if (subscriptionId == null) {
                subscriptionId = SubscriptionId.generate();
            }
            if (autoRenew == null) {
                autoRenew = true;
            }
            return new CreateSubscriptionCommand(
                subscriptionId, customerId, planId, billingCycle, autoRenew, trialDays
            );
        }
    }
}