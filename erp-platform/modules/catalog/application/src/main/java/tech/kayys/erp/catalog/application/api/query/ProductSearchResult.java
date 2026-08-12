package tech.kayys.erp.catalog.application.api.query;

import java.util.List;

/**
 * Product search result with pagination.
 */
public record ProductSearchResult(
        List<ProductSearchHit> hits,
        long totalCount,
        int page,
        int size,
        int totalPages,
        List<Facet> facets,
        boolean hasNext,
        boolean hasPrevious
) {

    public static ProductSearchResult of(
            List<ProductSearchHit> hits,
            long totalCount,
            int page,
            int size,
            List<Facet> facets) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        return new ProductSearchResult(
            hits,
            totalCount,
            page,
            size,
            totalPages,
            facets,
            page < totalPages - 1,
            page > 0
        );
    }

    /**
     * Product search hit.
     */
    public record ProductSearchHit(
            String productId,
            String name,
            String sku,
            String description,
            String price,
            String currencyCode,
            String imageUrl,
            double rating,
            int reviewCount,
            boolean inStock,
            List<String> categories,
            List<AttributeValue> attributes,
            double score
    ) {}

    /**
     * Attribute value.
     */
    public record AttributeValue(
            String name,
            String value,
            boolean filterable
    ) {}

    /**
     * Search facet.
     */
    public record Facet(
            String name,
            String type,
            List<FacetValue> values
    ) {}

    /**
     * Facet value.
     */
    public record FacetValue(
            String value,
            long count,
            boolean selected
    ) {}
}