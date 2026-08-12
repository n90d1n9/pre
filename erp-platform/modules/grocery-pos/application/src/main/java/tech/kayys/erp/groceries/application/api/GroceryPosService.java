package tech.kayys.erp.groceries.application.api;

import tech.kayys.erp.groceries.application.api.command.*;
import tech.kayys.erp.groceries.application.api.query.*;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;
import tech.kayys.erp.groceries.domain.valueobject.Weight;

import java.util.concurrent.CompletionStage;

/**
 * Public API for grocery POS operations.
 */
public interface GroceryPosService {

    // ============ Product Operations ============

    CompletionStage<GroceryProductId> registerGroceryProduct(RegisterGroceryProductCommand command);

    CompletionStage<GroceryProductId> addBatchLot(AddBatchLotCommand command);

    CompletionStage<GroceryProductId> updateShelfLife(UpdateShelfLifeCommand command);

    // ============ Scale Operations ============

    CompletionStage<ScaleId> registerScale(RegisterScaleCommand command);

    CompletionStage<ScaleId> connectScale(ConnectScaleCommand command);

    CompletionStage<WeightReadResult> readWeight(ReadWeightCommand command);

    CompletionStage<ScaleId> tareScale(TareScaleCommand command);

    // ============ Checkout Operations ============

    CompletionStage<CartItemResult> addWeightedItemToCart(AddWeightedItemCommand command);

    CompletionStage<GroceryReceipt> completeGroceryTransaction(CompleteGroceryTransactionCommand command);

    // ============ Expiry Management ============

    CompletionStage<ExpiryListResult> getProductsExpiringSoon(GetExpiringProductsQuery query);

    CompletionStage<Void> markProductsExpired(MarkExpiredProductsCommand command);

    CompletionStage<Void> processWaste(ProcessWasteCommand command);
}
