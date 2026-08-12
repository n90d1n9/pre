package tech.kayys.erp.promotion.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.promotion.application.api.command.CreatePromotionCommand;
import tech.kayys.erp.promotion.domain.identifier.PromotionId;
import tech.kayys.erp.promotion.domain.model.Promotion;
import tech.kayys.erp.promotion.domain.repository.PromotionRepository;
import tech.kayys.erp.promotion.domain.valueobject.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating promotions.
 */
@UseCase("Create a new promotion")
public class CreatePromotionHandler implements CommandHandler<CreatePromotionCommand, PromotionId> {

    private final PromotionRepository promotionRepository;

    @Inject
    public CreatePromotionHandler(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public CompletionStage<PromotionId> handle(CreatePromotionCommand command) {
        // Check if promo code is unique
        if (command.promoCode() != null && !command.promoCode().trim().isEmpty()) {
            return promotionRepository.findByPromoCode(command.promoCode())
                .thenCompose(existing -> {
                    if (existing != null) {
                        return CompletableFuture.failedFuture(
                            new IllegalArgumentException("Promo code already exists: " + command.promoCode())
                        );
                    }
                    return createPromotion(command);
                });
        }
        return createPromotion(command);
    }

    private CompletionStage<PromotionId> createPromotion(CreatePromotionCommand command) {
        // Create the promotion
        Promotion promotion = Promotion.create(
            command.promotionId(),
            command.name(),
            command.promotionType(),
            new BigDecimal(command.discountValue()),
            command.currencyCode(),
            command.startDate(),
            command.endDate()
        );

        // Set optional fields
        if (command.description() != null) {
            promotion.setDescription(command.description());
        }
        if (command.promoCode() != null) {
            promotion.setPromoCode(command.promoCode());
        }
        if (command.minimumOrderAmount() != null) {
            promotion.setMinimumOrderAmount(
                Money.of(command.minimumOrderAmount(), command.currencyCode())
            );
        }
        if (command.maximumDiscountAmount() != null) {
            promotion.setMaximumDiscountAmount(
                Money.of(command.maximumDiscountAmount(), command.currencyCode())
            );
        }
        if (command.targetAudience() != null) {
            promotion.setTargetAudience(command.targetAudience());
        }
        if (command.applicableProductIds() != null) {
            promotion.setApplicableProductIds(command.applicableProductIds());
        }
        if (command.applicableCategories() != null) {
            promotion.setApplicableCategories(command.applicableCategories());
        }
        
        promotion.setUsageLimitPerCustomer(command.usageLimitPerCustomer());
        promotion.setTotalUsageLimit(command.totalUsageLimit());
        promotion.setStackable(command.stackable());
        promotion.setPriority(command.priority());
        promotion.setRequiresCoupon(command.requiresCoupon());
        
        if (command.termsAndConditions() != null) {
            promotion.setTermsAndConditions(command.termsAndConditions());
        }
        if (command.createdBy() != null) {
            promotion.setCreatedBy(command.createdBy());
        }

        // If start date is in the future, schedule it
        if (command.startDate().isAfter(Instant.now())) {
            promotion.schedule();
        }

        // Save the promotion
        return promotionRepository.save(promotion)
            .thenApply(Promotion::getId);
    }
}