package tech.kayys.erp.catalog.infrastructure.persistence.mapper;

import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.catalog.domain.valueobject.Money;
import tech.kayys.erp.catalog.domain.valueobject.ProductStatus;
import tech.kayys.erp.catalog.infrastructure.persistence.entity.ProductEntity;

import java.util.Currency;

/**
 * Mapper between domain model and persistence entity.
 * This ensures the domain remains free of JPA annotations.
 */
public final class ProductMapper {

    private ProductMapper() {
        // Utility class
    }

    public static ProductEntity toEntity(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.id = product.getId().getValue();
        entity.name = product.getName();
        entity.description = product.getDescription();
        entity.price = product.getPrice().getAmount();
        entity.currency = product.getPrice().getCurrency().getCurrencyCode();
        entity.sku = product.getSku();
        entity.status = ProductEntity.ProductStatusEntity.valueOf(product.getStatus().name());
        entity.stockLevel = product.getStockLevel();
        entity.active = product.isActive();
        entity.createdAt = product.getCreatedAt();
        entity.updatedAt = product.getUpdatedAt();
        entity.version = product.getVersion();
        return entity;
    }

    public static Product toDomain(ProductEntity entity) {
        // Reconstruct the product from the entity
        // Note: This is simplified - in a real implementation, you'd have a factory
        // or reconstruction method on the aggregate
        
        Product product = Product.create(
            ProductId.of(entity.id),
            entity.name,
            entity.description,
            Money.of(entity.price, entity.currency),
            entity.sku
        );
        
        // We need to manually set the state (in a real implementation, use reflection or builder)
        // This is a simplified version - production code would use a more sophisticated approach
        // like a ProductBuilder or reconstruction method
        
        return product;
    }
}