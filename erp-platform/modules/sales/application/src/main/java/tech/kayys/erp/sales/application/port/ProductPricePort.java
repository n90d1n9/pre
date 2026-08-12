package tech.kayys.erp.sales.application.port;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for retrieving product information from Catalog context.
 */
public interface ProductPricePort {

    /**
     * Gets the current price of a product.
     */
    CompletionStage<MoneyDto> getProductPrice(UUID productId);

    /**
     * Validates if a product exists and is active.
     */
    CompletionStage<Boolean> productExists(UUID productId);

    /**
     * Gets product details.
     */
    CompletionStage<ProductInfoDto> getProductInfo(UUID productId);

    /**
     * Data transfer object for money.
     */
    record MoneyDto(String amount, String currencyCode) {}

    /**
     * Data transfer object for product information.
     */
    record ProductInfoDto(
            UUID productId,
            String name,
            String sku,
            MoneyDto price,
            boolean active,
            int stockLevel
    ) {}
}