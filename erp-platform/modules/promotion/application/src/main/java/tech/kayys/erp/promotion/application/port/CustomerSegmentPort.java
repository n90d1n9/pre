package tech.kayys.erp.promotion.application.port;

import tech.kayys.erp.promotion.domain.valueobject.TargetAudience;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for customer segmentation information.
 */
public interface CustomerSegmentPort {

    /**
     * Gets the customer segment for a customer.
     */
    CompletionStage<TargetAudience.CustomerSegment> getCustomerSegment(UUID customerId);

    /**
     * Checks if a customer is eligible for a target audience.
     */
    default CompletionStage<Boolean> isCustomerEligible(UUID customerId, TargetAudience audience) {
        return getCustomerSegment(customerId)
            .thenApply(segment -> audience.matchesCustomer(segment));
    }

    /**
     * Gets customer loyalty tier.
     */
    CompletionStage<LoyaltyTier> getCustomerLoyaltyTier(UUID customerId);

    record LoyaltyTier(
        String tier,
        int points,
        double discountRate
    ) {}
}