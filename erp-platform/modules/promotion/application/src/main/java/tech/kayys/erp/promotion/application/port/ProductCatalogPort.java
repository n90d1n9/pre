package tech.kayys.erp.promotion.application.port;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for product catalog information.
 */
public interface ProductCatalogPort {

    /**
     * Gets product details.
     */
    CompletionStage<ProductDetails> getProductDetails(UUID productId);

    /**
     * Gets product categories.
     */
    CompletionStage<List<String>> getProductCategories(UUID productId);

    /**
     * Checks if products belong to a category.
     */
    CompletionStage<Boolean> productsInCategory(List<UUID> productIds, String category);

    record ProductDetails(
        UUID productId,
        String name,
        String sku,
        String price,
        String currencyCode,
        List<String> categories,
        boolean active
    ) {}
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>
</modules>