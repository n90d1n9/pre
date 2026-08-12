package tech.kayys.erp.inventory.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.inventory.domain.identifier.ProductId;
import tech.kayys.erp.inventory.domain.identifier.WarehouseId;
import tech.kayys.erp.inventory.domain.model.InventoryItem;
import tech.kayys.erp.inventory.domain.valueobject.InventoryStatus;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for InventoryItem aggregates.
 */
public interface InventoryItemRepository extends Repository<InventoryItem, ProductId> {

    /**
     * Finds inventory items by warehouse.
     */
    CompletionStage<List<InventoryItem>> findByWarehouse(WarehouseId warehouseId);

    /**
     * Finds inventory items by status.
     */
    CompletionStage<List<InventoryItem>> findByStatus(InventoryStatus status);

    /**
     * Finds inventory items by product.
     */
    CompletionStage<List<InventoryItem>> findByProduct(ProductId productId);

    /**
     * Finds inventory items by product and warehouse.
     */
    CompletionStage<InventoryItem> findByProductAndWarehouse(
        ProductId productId,
        WarehouseId warehouseId
    );

    /**
     * Finds inventory items with low stock (needs reorder).
     */
    CompletionStage<List<InventoryItem>> findLowStockItems();

    /**
     * Finds inventory items by expiry date.
     */
    CompletionStage<List<InventoryItem>> findExpiringItems(Instant beforeDate);

    /**
     * Counts items in a warehouse.
     */
    CompletionStage<Long> countByWarehouse(WarehouseId warehouseId);

    /**
     * Counts items by status.
     */
    CompletionStage<Long> countByStatus(InventoryStatus status);
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

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>
</modules>