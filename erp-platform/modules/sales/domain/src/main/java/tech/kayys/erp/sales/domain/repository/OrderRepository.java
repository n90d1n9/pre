package tech.kayys.erp.sales.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Order aggregates.
 * Provides domain-specific query methods beyond the generic Repository interface.
 */
public interface OrderRepository extends Repository<Order, OrderId> {

    /**
     * Finds all orders for a customer.
     */
    CompletionStage<List<Order>> findByCustomerId(CustomerId customerId);

    /**
     * Finds orders by status.
     */
    CompletionStage<List<Order>> findByStatus(OrderStatus status);

    /**
     * Finds orders submitted between two dates.
     */
    CompletionStage<List<Order>> findSubmittedBetween(Instant start, Instant end);

    /**
     * Finds orders that need processing (submitted or confirmed).
     */
    default CompletionStage<List<Order>> findOrdersNeedingProcessing() {
        return findByStatus(OrderStatus.SUBMITTED)
            .thenCombine(findByStatus(OrderStatus.CONFIRMED), 
                (submitted, confirmed) -> {
                    submitted.addAll(confirmed);
                    return submitted;
                });
    }

    /**
     * Counts orders by status.
     */
    CompletionStage<Long> countByStatus(OrderStatus status);

    /**
     * Checks if a customer has any active orders.
     */
    default CompletionStage<Boolean> customerHasActiveOrders(CustomerId customerId) {
        return findByCustomerId(customerId)
            .thenApply(orders -> orders.stream()
                .anyMatch(order -> order.getStatus().isActive())
            );
    }
}