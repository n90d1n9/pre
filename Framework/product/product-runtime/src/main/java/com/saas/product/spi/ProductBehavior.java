package com.saas.product.spi;

import com.saas.product.core.ProductAggregate;
import com.saas.product.core.pricing.PricingContext;
import com.saas.product.core.pricing.PricingResult;

/**
 * Strategy interface for domain-specific product behaviors.
 *
 * Each domain (ecommerce, fnb, subscription) provides an implementation
 * that handles pricing logic and any other domain-specific operations.
 *
 * Discovered via CDI @Any injection or ServiceLoader.
 * Keyed by {@link #getContext()} matching ProductExtension.getContext().
 */
public interface ProductBehavior {

    /**
     * Context key matching the extension this behavior handles.
     * Must match the corresponding ProductExtension.getContext().
     */
    String getContext();

    /**
     * Calculate the price for a product in this context.
     *
     * @param product  the full aggregate (core + extensions)
     * @param ctx      pricing context (qty, channel, segment, coupons)
     * @return         a fully computed, auditable PricingResult
     */
    PricingResult calculatePrice(ProductAggregate product, PricingContext ctx);

    /**
     * Optional: validate context-specific constraints beyond the base validator.
     * Default is a no-op (validators handle this separately).
     */
    default void assertOrderable(ProductAggregate product) {
        if (!product.isOrderable()) {
            throw new IllegalStateException(
                    "Product " + product.getId() + " is not orderable (status=" + product.getStatus() + ")");
        }
    }
}
