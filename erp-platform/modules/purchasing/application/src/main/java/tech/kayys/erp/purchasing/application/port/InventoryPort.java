package tech.kayys.erp.purchasing.application.port;

import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;

import java.util.concurrent.CompletionStage;

/**
 * Port for inventory operations.
 */
public interface InventoryPort {

    /**
     * Receives a purchase order into inventory.
     */
    CompletionStage<Void> receivePurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * Validates inventory availability for a purchase order.
     */
    CompletionStage<Boolean> validateInventoryAvailability(PurchaseOrder purchaseOrder);

    /**
     * Gets inventory forecast for a product.
     */
    CompletionStage<InventoryForecast> getInventoryForecast(UUID productId);

    record InventoryForecast(
        UUID productId,
        int currentStock,
        int reorderPoint,
        int reorderQuantity,
        String leadTime
    ) {}
}