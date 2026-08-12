package tech.kayys.erp.pricing.application.service;

import io.smallrye.mutiny.Uni;
import tech.kayys.erp.pricing.domain.model.*;
import tech.kayys.erp.pricing.domain.repository.*;
import tech.kayys.erp.pricing.domain.valueobject.Money;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Complete Pricing Service with all pricing capabilities.
 */
@ApplicationScoped
public class PricingService {

    @Inject
    PriceBookRepository priceBookRepository;

    @Inject
    DynamicPriceRuleRepository dynamicPriceRuleRepository;

    @Inject
    TieredPriceRepository tieredPriceRepository;

    @Inject
    PriceHistoryRepository priceHistoryRepository;

    /**
     * Gets the price for a product with all applicable rules.
     */
    public Uni<PriceResult> getPrice(
            String productId,
            double quantity,
            String customerSegment,
            String region,
            String channel,
            Map<String, Object> context) {
        
        // 1. Get base price from price book
        return priceBookRepository.findActivePriceBook(customerSegment, region, channel)
            .onItem()
            .transformToUni(priceBook -> {
                if (priceBook == null) {
                    return Uni.createFrom().failure(
                        new IllegalArgumentException("No active price book found")
                    );
                }

                Money basePrice = priceBook.getPrice(productId);
                if (basePrice == null) {
                    return Uni.createFrom().failure(
                        new IllegalArgumentException("Product not found in price book")
                    );
                }

                // 2. Check tiered pricing
                return tieredPriceRepository.findByProductId(productId)
                    .onItem()
                    .transformToUni(tieredPrice -> {
                        Money tieredPrice = null;
                        if (tieredPrice != null && tieredPrice.isActive()) {
                            tieredPrice = tieredPrice.getUnitPriceForQuantity(quantity);
                        }

                        // 3. Apply dynamic pricing rules
                        return dynamicPriceRuleRepository.findActiveRulesForProduct(productId)
                            .onItem()
                            .transformToUni(rules -> {
                                Money finalPrice = tieredPrice != null ? tieredPrice : basePrice;
                                
                                // Sort rules by priority
                                rules.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
                                
                                for (DynamicPriceRule rule : rules) {
                                    finalPrice = rule.calculatePrice(finalPrice, context);
                                }

                                // 4. Record price calculation
                                return recordPriceCalculation(productId, finalPrice, basePrice, quantity)
                                    .onItem()
                                    .transform(v -> new PriceResult(
                                        productId,
                                        basePrice,
                                        tieredPrice,
                                        finalPrice,
                                        quantity,
                                        priceBook.getId().toString(),
                                        Instant.now()
                                    ));
                            });
                    });
            });
    }

    private Uni<Void> recordPriceCalculation(
            String productId,
            Money newPrice,
            Money oldPrice,
            double quantity) {
        // In production, record to price history
        return Uni.createFrom().voidItem();
    }

    /**
     * Price result record.
     */
    public record PriceResult(
        String productId,
        Money basePrice,
        Money tieredPrice,
        Money finalPrice,
        double quantity,
        String priceBookId,
        Instant calculatedAt
    ) {}
}