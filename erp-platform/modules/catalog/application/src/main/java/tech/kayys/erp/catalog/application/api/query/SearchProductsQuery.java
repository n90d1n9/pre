package tech.kayys.erp.catalog.application.api.query;

import tech.kayys.erp.foundation.application.Query;

import java.util.List;

/**
 * Query to search for products with filters.
 */
public record SearchProductsQuery(
        String nameContains,
        String skuStartsWith,
        Double minPrice,
        Double maxPrice,
        String currencyCode,
        Boolean activeOnly,
        int page,
        int size,
        SortBy sortBy
) implements Query<List<ProductView>> {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    public SearchProductsQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }

    public enum SortBy {
        NAME_ASC,
        NAME_DESC,
        PRICE_ASC,
        PRICE_DESC,
        CREATED_AT_ASC,
        CREATED_AT_DESC
    }

    public static SearchProductsQuery defaultQuery() {
        return new SearchProductsQuery(
            null, null, null, null, null, true, 
            DEFAULT_PAGE, DEFAULT_SIZE, SortBy.NAME_ASC
        );
    }

    public SearchProductsQuery withPage(int page) {
        return new SearchProductsQuery(
            nameContains, skuStartsWith, minPrice, maxPrice, 
            currencyCode, activeOnly, page, size, sortBy
        );
    }

    public SearchProductsQuery withSize(int size) {
        return new SearchProductsQuery(
            nameContains, skuStartsWith, minPrice, maxPrice, 
            currencyCode, activeOnly, page, size, sortBy
        );
    }
}