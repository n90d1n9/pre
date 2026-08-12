package tech.kayys.erp.promotion.application.api;

import tech.kayys.erp.promotion.application.api.command.*;
import tech.kayys.erp.promotion.application.api.query.PromotionView;
import tech.kayys.erp.promotion.application.api.query.PromotionValidationResult;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for promotion operations.
 */
public interface PromotionService {

    // ============ Promotion Commands ============

    /**
     * Creates a new promotion.
     */
    CompletionStage<PromotionId> createPromotion(CreatePromotionCommand command);

    /**
     * Activates a promotion.
     */
    CompletionStage<PromotionId> activatePromotion(ActivatePromotionCommand command);

    /**
     * Pauses a promotion.
     */
    CompletionStage<PromotionId> pausePromotion(PausePromotionCommand command);

    /**
     * Schedules a promotion.
     */
    CompletionStage<PromotionId> schedulePromotion(SchedulePromotionCommand command);

    /**
     * Completes a promotion.
     */
    CompletionStage<PromotionId> completePromotion(CompletePromotionCommand command);

    /**
     * Cancels a promotion.
     */
    CompletionStage<PromotionId> cancelPromotion(CancelPromotionCommand command);

    /**
     * Redeems a promotion.
     */
    CompletionStage<PromotionId> redeemPromotion(RedeemPromotionCommand command);

    // ============ Promotion Queries ============

    /**
     * Validates if a promotion is applicable for a customer.
     */
    CompletionStage<PromotionValidationResult> validatePromotion(
        String promoCode, UUID customerId, String orderAmount
    );

    /**
     * Gets active promotions for a customer.
     */
    CompletionStage<List<PromotionView>> getActivePromotions(UUID customerId);

    /**
     * Gets promotion by ID.
     */
    CompletionStage<PromotionView> getPromotion(PromotionId promotionId);

    /**
     * Gets all applicable promotions for an order.
     */
    CompletionStage<List<PromotionView>> getApplicablePromotions(
        UUID customerId, List<UUID> productIds, String orderAmount
    );

    // ============ Campaign Commands ============

    /**
     * Creates a new marketing campaign.
     */
    CompletionStage<CampaignId> createCampaign(CreateCampaignCommand command);

    /**
     * Launches a campaign.
     */
    CompletionStage<CampaignId> launchCampaign(LaunchCampaignCommand command);

    /**
     * Ends a campaign.
     */
    CompletionStage<CampaignId> endCampaign(EndCampaignCommand command);

    /**
     * Records campaign metrics.
     */
    CompletionStage<CampaignId> recordCampaignMetrics(RecordCampaignMetricsCommand command);
}