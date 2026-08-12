package tech.kayys.erp.sales.application.port;

import tech.kayys.erp.sales.domain.model.Order;

import java.util.concurrent.CompletionStage;

/**
 * Port for reserving inventory from the Inventory context.
 */
public interface ReserveInventoryPort {

    /**
     * Reserves inventory for an order.
     */
    CompletionStage<Void> reserveForOrder(Order order);

    /**
     * Releases inventory reserved for an order.
     */
    CompletionStage<Void> releaseReservation(Order order);

    /**
     * Checks if inventory is available for an order.
     */
    CompletionStage<Boolean> isInventoryAvailable(Order order);

    /**
     * Gets inventory reservation status.
     */
    CompletionStage<ReservationStatus> getReservationStatus(Order order);

    enum ReservationStatus {
        PENDING,
        RESERVED,
        CONFIRMED,
        RELEASED,
        FAILED
    }
}