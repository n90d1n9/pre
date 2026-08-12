package tech.kayys.erp.purchasing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;
import tech.kayys.erp.purchasing.domain.valueobject.PurchaseOrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for PurchaseOrder aggregates.
 */
public interface PurchaseOrderRepository extends Repository<PurchaseOrder, PurchaseOrderId> {

    /**
     * Finds purchase orders by vendor.
     */
    CompletionStage<List<PurchaseOrder>> findByVendorId(VendorId vendorId);

    /**
     * Finds purchase orders by status.
     */
    CompletionStage<List<PurchaseOrder>> findByStatus(PurchaseOrderStatus status);

    /**
     * Finds active purchase orders (non-terminal).
     */
    default CompletionStage<List<PurchaseOrder>> findActiveOrders() {
        return findByStatus(PurchaseOrderStatus.SUBMITTED)
            .thenCombine(findByStatus(PurchaseOrderStatus.ACKNOWLEDGED),
                (submitted, acknowledged) -> {
                    submitted.addAll(acknowledged);
                    return submitted;
                })
            .thenCombine(findByStatus(PurchaseOrderStatus.IN_TRANSIT),
                (combined, inTransit) -> {
                    combined.addAll(inTransit);
                    return combined;
                })
            .thenCombine(findByStatus(PurchaseOrderStatus.PARTIALLY_RECEIVED),
                (combined, partial) -> {
                    combined.addAll(partial);
                    return combined;
                });
    }

    /**
     * Finds purchase orders due for delivery.
     */
    CompletionStage<List<PurchaseOrder>> findOrdersDueForDelivery(Instant date);

    /**
     * Finds purchase orders by date range.
     */
    CompletionStage<List<PurchaseOrder>> findByDateRange(Instant start, Instant end);

    /**
     * Finds purchase orders requiring approval.
     */
    CompletionStage<List<PurchaseOrder>> findOrdersRequiringApproval();

    /**
     * Finds purchase orders with items to receive.
     */
    CompletionStage<List<PurchaseOrder>> findOrdersWithItemsToReceive();

    /**
     * Finds overdue purchase orders.
     */
    CompletionStage<List<PurchaseOrder>> findOverdueOrders(Instant currentDate);

    /**
     * Generates a unique PO number.
     */
    CompletionStage<String> generatePoNumber();
}