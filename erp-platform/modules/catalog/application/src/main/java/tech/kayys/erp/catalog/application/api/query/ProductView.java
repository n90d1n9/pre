package tech.kayys.erp.catalog.application.api.query;

import tech.kayys.erp.catalog.domain.model.Product;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read-only view of a product for query responses.
 */
public record ProductView(
        String id,
        String name,
        String description,
        BigDecimal price,
        String currencyCode,
        String sku,
        String status,
        int stockLevel,
        boolean active,
        boolean available,
        Instant createdAt,
        Instant updatedAt
) {
    
    public static ProductView fromDomain(Product product) {
        return new ProductView(
            product.getId().toString(),
            product.getName(),
            product.getDescription(),
            product.getPrice().getAmount(),
            product.getPrice().getCurrency().getCurrencyCode(),
            product.getSku(),
            product.getStatus().name(),
            product.getStockLevel(),
            product.isActive(),
            product.isAvailable(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
}