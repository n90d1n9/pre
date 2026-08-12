package tech.kayys.erp.groceries.application.api;

import tech.kayys.erp.groceries.application.api.command.*;
import tech.kayys.erp.groceries.application.api.query.*;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for grocery POS operations.
 */
public interface GroceryPosService {

    // ============ Product Operations ============

    /**
     * Registers a grocery product in the system.
     */
    CompletionStage<GroceryProductId> registerGroceryProduct(RegisterGroceryProductCommand command);

    /**
     * Adds a batch/lot to a product.
     */
    CompletionStage<GroceryProductId> addBatchLot(AddBatchLotCommand command);

    /**
     * Updates product shelf life.
     */
    CompletionStage<GroceryProductId> updateShelfLife(UpdateShelfLifeCommand command);

    // ============ Scale Operations ============

    /**
     * Registers a scale device.
     */
    CompletionStage<ScaleId> registerScale(RegisterScaleCommand command);

    /**
     * Connects a scale device.
     */
    CompletionStage<ScaleId> connectScale(ConnectScaleCommand command);

    /**
     * Reads a weight from the scale.
     */
    CompletionStage<WeightReadResult> readWeight(ReadWeightCommand command);

    /**
     * Tares the scale.
     */
    CompletionStage<ScaleId> tareScale(TareScaleCommand command);

    // ============ Checkout Operations ============

    /**
     * Adds a weighted item to the cart.
     */
    CompletionStage<CartItemResult> addWeightedItemToCart(AddWeightedItemCommand command);

    /**
     * Completes a grocery POS transaction.
     */
    CompletionStage<GroceryReceipt> completeGroceryTransaction(CompleteGroceryTransactionCommand command);

    // ============ Expiry Management ============

    /**
     * Gets products expiring soon.
     */
    CompletionStage<ExpiryListResult> getProductsExpiringSoon(GetExpiringProductsQuery query);

    /**
     * Marks products as expired.
     */
    CompletionStage<Void> markProductsExpired(MarkExpiredProductsCommand command);

    /**
     * Processes waste/write-off for expired products.
     */
    CompletionStage<Void> processWaste(ProcessWasteCommand command);
}