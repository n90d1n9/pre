package tech.kayys.erp.purchasing.application.port;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for sales order information.
 */
public interface SalesOrderPort {

    /**
     * Gets sales order details for creating a purchase order.
     */
    CompletionStage<SalesOrderDetails> getSalesOrderDetails(UUID salesOrderId);

    record SalesOrderDetails(
        UUID salesOrderId,
        UUID customerId,
        List<SalesOrderItem> items,
        String currencyCode
    ) {}

    record SalesOrderItem(
        UUID productId,
        String productName,
        String sku,
        int quantity,
        String unitPrice
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
</modules>