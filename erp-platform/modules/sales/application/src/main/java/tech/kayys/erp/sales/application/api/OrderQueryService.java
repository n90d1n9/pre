package tech.kayys.erp.sales.application.api;

import tech.kayys.erp.sales.application.api.query.GetOrderQuery;
import tech.kayys.erp.sales.application.api.query.OrderView;
import tech.kayys.erp.sales.application.api.query.SearchOrdersQuery;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for order queries.
 */
public interface OrderQueryService {

    /**
     * Retrieves an order by ID.
     */
    CompletionStage<OrderView> getOrder(GetOrderQuery query);

    /**
     * Searches for orders.
     */
    CompletionStage<List<OrderView>> searchOrders(SearchOrdersQuery query);

    /**
     * Checks if an order exists.
     */
    CompletionStage<Boolean> orderExists(tech.kayys.erp.sales.domain.identifier.OrderId orderId);

    /**
     * Gets orders for a customer.
     */
    default CompletionStage<List<OrderView>> getCustomerOrders(
            tech.kayys.erp.sales.domain.identifier.CustomerId customerId) {
        SearchOrdersQuery query = SearchOrdersQuery.defaultQuery()
            .forCustomer(customerId)
            .withSize(100);
        return searchOrders(query);
    }
}