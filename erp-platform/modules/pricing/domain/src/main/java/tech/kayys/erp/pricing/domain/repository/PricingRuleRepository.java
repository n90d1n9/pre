package tech.kayys.erp.pricing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.pricing.domain.identifier.PricingRuleId;
import tech.kayys.erp.pricing.domain.model.PricingRule;
import tech.kayys.erp.pricing.domain.valueobject.DiscountType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Repository for PricingRule aggregates.
 */
public interface PricingRuleRepository extends Repository<PricingRule, PricingRuleId> {

    /**
     * Finds all active pricing rules.
     */
    CompletionStage<List<PricingRule>> findActiveRules();

    /**
     * Finds pricing rules applicable to a product.
     */
    CompletionStage<List<PricingRule>> findApplicableRules(UUID productId);

    /**
     * Finds pricing rules by type.
     */
    CompletionStage<List<PricingRule>> findByType(DiscountType type);

    /**
     * Finds pricing rules by coupon code.
     */
    CompletionStage<PricingRule> findByCouponCode(String couponCode);

    /**
     * Finds pricing rules valid at a given time.
     */
    CompletionStage<List<PricingRule>> findValidAt(Instant time);

    /**
     * Finds pricing rules with a specific priority.
     */
    CompletionStage<List<PricingRule>> findByPriorityGreaterThan(int priority);
}