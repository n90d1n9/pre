package tech.kayys.erp.subscription.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.model.SubscriptionPlan;
import tech.kayys.erp.subscription.domain.valueobject.PlanType;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for SubscriptionPlan aggregates.
 */
public interface SubscriptionPlanRepository extends Repository<SubscriptionPlan, PlanId> {

    /**
     * Finds all active plans.
     */
    CompletionStage<List<SubscriptionPlan>> findActivePlans();

    /**
     * Finds plans by type.
     */
    CompletionStage<List<SubscriptionPlan>> findByType(PlanType planType);

    /**
     * Finds public plans (available for purchase).
     */
    CompletionStage<List<SubscriptionPlan>> findPublicPlans();

    /**
     * Finds plans sorted by price.
     */
    CompletionStage<List<SubscriptionPlan>> findAllSortedByPrice(boolean ascending);

    /**
     * Checks if a plan name is unique.
     */
    CompletionStage<Boolean> isPlanNameUnique(String name);
}