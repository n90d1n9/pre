package tech.kayys.erp.purchasing.application.api;

import tech.kayys.erp.purchasing.application.api.command.*;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for purchase order commands.
 */
public interface PurchaseOrderCommandService {

    /**
     * Creates a new purchase order.
     */
    CompletionStage<PurchaseOrderId> createPurchaseOrder(CreatePurchaseOrderCommand command);

    /**
     * Submits a purchase order to the vendor.
     */
    CompletionStage<PurchaseOrderId> submitPurchaseOrder(SubmitPurchaseOrderCommand command);

    /**
     * Acknowledges a purchase order from the vendor.
     */
    CompletionStage<PurchaseOrderId> acknowledgePurchaseOrder(AcknowledgePurchaseOrderCommand command);

    /**
     * Marks a purchase order as in transit.
     */
    CompletionStage<PurchaseOrderId> markInTransit(MarkInTransitCommand command);

    /**
     * Receives items for a purchase order.
     */
    CompletionStage<PurchaseOrderId> receiveItems(ReceivePurchaseOrderItemsCommand command);

    /**
     * Completes a purchase order.
     */
    CompletionStage<PurchaseOrderId> completePurchaseOrder(CompletePurchaseOrderCommand command);

    /**
     * Cancels a purchase order.
     */
    CompletionStage<PurchaseOrderId> cancelPurchaseOrder(CancelPurchaseOrderCommand command);

    /**
     * Places a purchase order on hold.
     */
    CompletionStage<PurchaseOrderId> holdPurchaseOrder(HoldPurchaseOrderCommand command);

    /**
     * Creates a purchase order from a sales order.
     */
    CompletionStage<PurchaseOrderId> createFromSalesOrder(CreateFromSalesOrderCommand command);
}