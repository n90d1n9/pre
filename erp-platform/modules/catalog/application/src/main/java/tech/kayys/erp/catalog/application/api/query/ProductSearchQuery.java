package tech.kayys.erp.catalog.application.api.query;

import tech.kayys.erp.foundation.application.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Advanced product search query.
 */
public record ProductSearchQuery(
        String searchTerm,
        List<UUID> categoryIds,
        List<String> productTypes,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        String currencyCode,
        List<String> brands,
        List<String> attributes, // attribute:value format
        Boolean inStock,
        Boolean active,
        String sortBy,
        SortDirection sortDirection,
        int page,
        int size
) implements Query<ProductSearchResult> {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;

    public ProductSearchQuery {
        if (page < 0) {
            throw new IllegalArgumentException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
    }

    public enum SortDirection {
        ASC, DESC
    }

    public static ProductSearchQuery defaultQuery() {
        return new ProductSearchQuery(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            DEFAULT_PAGE,
            DEFAULT_SIZE
        );
    }

    public ProductSearchQuery withSearchTerm(String searchTerm) {
        return new ProductSearchQuery(
            searchTerm,
            categoryIds,
            productTypes,
            minPrice,
            maxPrice,
            currencyCode,
            brands,
            attributes,
            inStock,
            active,
            sortBy,
            sortDirection,
            page,
            size
        );
    }

    public ProductSearchQuery withCategory(UUID categoryId) {
        return new ProductSearchQuery(
            searchTerm,
            categoryIds != null ? List.of(categoryId) : List.of(categoryId),
            productTypes,
            minPrice,
            maxPrice,
            currencyCode,
            brands,
            attributes,
            inStock,
            active,
            sortBy,
            sortDirection,
            page,
            size
        );
    }

    public ProductSearchQuery withPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return new ProductSearchQuery(
            searchTerm,
            categoryIds,
            productTypes,
            minPrice,
            maxPrice,
            currencyCode,
            brands,
            attributes,
            inStock,
            active,
            sortBy,
            sortDirection,
            page,
            size
        );
    }

    public ProductSearchQuery withPage(int page) {
        return new ProductSearchQuery(
            searchTerm,
            categoryIds,
            productTypes,
            minPrice,
            maxPrice,
            currencyCode,
            brands,
            attributes,
            inStock,
            active,
            sortBy,
            sortDirection,
            page,
            size
        );
    }

    public ProductSearchQuery withSize(int size) {
        return new ProductSearchQuery(
            searchTerm,
            categoryIds,
            productTypes,
            minPrice,
            maxPrice,
            currencyCode,
            brands,
            attributes,
            inStock,
            active,
            sortBy,
            sortDirection,
            page,
            size
        );
    }

    public ProductSearchQuery withSort(String sortBy, SortDirection sortDirection) {
        return new ProductSearchQuery(
            searchTerm,
            categoryIds,
            productTypes,
            minPrice,
            maxPrice,
            currencyCode,
            brands,
            attributes,
            inStock,
            active,
            sortBy,
            sortDirection,
            page,
            size
        );
    }
}