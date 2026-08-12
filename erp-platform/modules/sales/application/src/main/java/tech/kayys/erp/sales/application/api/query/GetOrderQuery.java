package tech.kayys.erp.sales.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Query to retrieve an order by ID.
 */
public record GetOrderQuery(
        OrderId orderId
) implements Query<OrderView> {

    public GetOrderQuery {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
    }
}