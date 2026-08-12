package tech.kayys.erp.catalog.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.catalog.domain.repository.ProductRepository;
import tech.kayys.erp.catalog.infrastructure.persistence.entity.ProductEntity;
import tech.kayys.erp.catalog.infrastructure.persistence.mapper.ProductMapper;
import tech.kayys.erp.foundation.persistence.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Reactive repository implementation for Product using Hibernate Reactive Panache.
 */
@ApplicationScoped
public class ProductRepositoryImpl extends BaseRepository<ProductEntity> 
        implements ProductRepository {

    private final ProductMapper mapper = ProductMapper.INSTANCE;

    @Override
    @WithTransaction
    public Uni<Product> save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        
        if (entity.id != null) {
            // Update existing
            return findById(entity.id)
                .chain(existing -> {
                    if (existing == null) {
                        return Uni.createFrom().failure(
                            new IllegalArgumentException("Product not found: " + product.getId())
                        );
                    }
                    // Update fields
                    existing.name = entity.name;
                    existing.description = entity.description;
                    existing.price = entity.price;
                    existing.currency = entity.currency;
                    existing.sku = entity.sku;
                    existing.status = entity.status;
                    existing.stockLevel = entity.stockLevel;
                    existing.active = entity.active;
                    existing.updatedAt = entity.updatedAt;
                    existing.version = entity.version;
                    
                    return persist(existing)
                        .onItem()
                        .transform(v -> {
                            product.clearEvents();
                            return product;
                        });
                });
        } else {
            // Insert new
            return persist(entity)
                .onItem()
                .transform(v -> {
                    product.clearEvents();
                    return product;
                });
        }
    }

    @Override
    public Uni<Optional<Product>> findById(ProductId id) {
        return findByIdOptional(id.getValue())
            .onItem()
            .transform(entityOpt -> entityOpt.map(mapper::toDomain));
    }

    @Override
    public Uni<Boolean> existsById(ProductId id) {
        return findById(id)
            .onItem()
            .transform(opt -> opt.isPresent());
    }

    @Override
    @WithTransaction
    public Uni<Void> delete(Product product) {
        return deleteById(product.getId().getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteById(ProductId id) {
        return deleteById(id.getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    public Uni<Boolean> existsBySku(String sku) {
        return count("sku = ?1", sku)
            .onItem()
            .transform(count -> count > 0);
    }

    /**
     * Finds products by category with pagination.
     */
    public Uni<Tuple2<List<Product>, Long>> findByCategory(UUID categoryId, int page, int size) {
        return Uni.combine()
            .all()
            .unis(
                find("categoryId = ?1 and active = true order by name", categoryId)
                    .page(page, size)
                    .list()
                    .onItem()
                    .transform(entities -> entities.stream()
                        .map(mapper::toDomain)
                        .collect(Collectors.toList())
                    ),
                count("categoryId = ?1 and active = true", categoryId)
            )
            .asTuple();
    }

    /**
     * Searches products by name or description.
     */
    public Uni<List<Product>> searchProducts(String searchTerm) {
        String query = "%" + searchTerm.toLowerCase() + "%";
        return find("lower(name) like ?1 or lower(description) like ?1 and active = true", query)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList())
            );
    }

    /**
     * Finds products with low stock.
     */
    public Uni<List<Product>> findLowStockProducts(int threshold) {
        return find("stockLevel <= ?1 and active = true order by stockLevel asc", threshold)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList())
            );
    }

    /**
     * Updates stock level atomically.
     */
    @WithTransaction
    public Uni<Product> updateStock(UUID productId, int newStockLevel) {
        return findById(productId)
            .chain(entity -> {
                if (entity == null) {
                    return Uni.createFrom().failure(
                        new IllegalArgumentException("Product not found: " + productId)
                    );
                }
                entity.stockLevel = newStockLevel;
                entity.updatedAt = Instant.now();
                return persist(entity)
                    .onItem()
                    .transform(mapper::toDomain);
            });
    }

    /**
     * Batch update stock levels.
     */
    @WithTransaction
    public Uni<List<Product>> batchUpdateStock(List<Tuple2<UUID, Integer>> stockUpdates) {
        return Uni.createFrom()
            .deferred(() -> {
                List<Uni<Product>> updates = stockUpdates.stream()
                    .map(tuple -> updateStock(tuple.getItem1(), tuple.getItem2()))
                    .collect(Collectors.toList());
                return Uni.combine()
                    .all()
                    .unis(updates)
                    .combinedWith(list -> list.stream()
                        .map(obj -> (Product) obj)
                        .collect(Collectors.toList())
                    );
            });
    }
}