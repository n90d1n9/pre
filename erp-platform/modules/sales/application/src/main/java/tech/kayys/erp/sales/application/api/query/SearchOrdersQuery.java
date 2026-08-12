package tech.kayys.erp.sales.application.api.query;

import tech.kayys.erp.foundation.application.Query;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import java.time.Instant;
import java.util.List;

/**
 * Query to search for orders.
 */
public record SearchOrdersQuery(
        CustomerId customerId,
        OrderStatus status,
        Instant fromDate,
        Instant toDate,
        Double minTotal,
        Double maxTotal,
        String currencyCode,
        int page,
        int size,
        SortBy sortBy
) implements Query<List<OrderView>> {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    public SearchOrdersQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }

    public enum SortBy {
        ORDER_DATE_ASC,
        ORDER_DATE_DESC,
        STATUS_ASC,
        STATUS_DESC,
        TOTAL_ASC,
        TOTAL_DESC
    }

    public static SearchOrdersQuery defaultQuery() {
        return new SearchOrdersQuery(
            null, null, null, null, null, null,
            DEFAULT_PAGE, DEFAULT_SIZE, SortBy.ORDER_DATE_DESC
        );
    }

    public SearchOrdersQuery withPage(int page) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }

    public SearchOrdersQuery withSize(int size) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }

    public SearchOrdersQuery withStatus(OrderStatus status) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }

    public SearchOrdersQuery forCustomer(CustomerId customerId) {
        return new SearchOrdersQuery(
            customerId, status, fromDate, toDate,
            minTotal, maxTotal, currencyCode,
            page, size, sortBy
        );
    }
}