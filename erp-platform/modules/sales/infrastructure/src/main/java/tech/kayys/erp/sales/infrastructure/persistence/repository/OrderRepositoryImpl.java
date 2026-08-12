package tech.kayys.erp.sales.infrastructure.persistence.repository;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import tech.kayys.erp.foundation.persistence.BaseRepository;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.repository.OrderRepository;
import tech.kayys.erp.sales.infrastructure.persistence.entity.OrderEntity;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.valueobject.OrderStatus;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Reactive repository implementation for Order.
 */
@ApplicationScoped
public class OrderRepositoryImpl extends BaseRepository<OrderEntity> 
        implements OrderRepository {

    @Override
    @WithTransaction
    public Uni<Order> save(Order order) {
        OrderEntity entity = OrderEntity.fromDomain(order);
        
        if (entity.id != null) {
            return findById(entity.id)
                .chain(existing -> {
                    if (existing == null) {
                        return Uni.createFrom().failure(
                            new IllegalArgumentException("Order not found: " + order.getId())
                        );
                    }
                    // Update existing order
                    existing.customerId = entity.customerId;
                    existing.items.clear();
                    existing.items.addAll(entity.items);
                    existing.subtotal = entity.subtotal;
                    existing.taxTotal = entity.taxTotal;
                    existing.shippingCost = entity.shippingCost;
                    existing.discountTotal = entity.discountTotal;
                    existing.grandTotal = entity.grandTotal;
                    existing.status = entity.status;
                    existing.updatedAt = entity.updatedAt;
                    existing.version = entity.version;
                    
                    return persist(existing)
                        .onItem()
                        .transform(v -> {
                            order.clearEvents();
                            return order;
                        });
                });
        } else {
            return persist(entity)
                .onItem()
                .transform(v -> {
                    order.clearEvents();
                    return order;
                });
        }
    }

    @Override
    public Uni<Optional<Order>> findById(OrderId id) {
        return findByIdOptional(id.getValue())
            .onItem()
            .transform(entityOpt -> entityOpt.map(OrderEntity::toDomain));
    }

    @Override
    public Uni<Boolean> existsById(OrderId id) {
        return findById(id)
            .onItem()
            .transform(opt -> opt.isPresent());
    }

    @Override
    @WithTransaction
    public Uni<Void> delete(Order order) {
        return deleteById(order.getId().getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    @WithTransaction
    public Uni<Void> deleteById(OrderId id) {
        return deleteById(id.getValue())
            .onItem()
            .transform(v -> null);
    }

    @Override
    public Uni<List<Order>> findByCustomerId(CustomerId customerId) {
        return find("customerId = ?1 order by createdAt desc", customerId.getValue())
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<Order>> findByStatus(OrderStatus status) {
        return find("status = ?1 order by createdAt desc", status.name())
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<List<Order>> findSubmittedBetween(Instant start, Instant end) {
        return find("submittedAt between ?1 and ?2 order by submittedAt desc", start, end)
            .list()
            .onItem()
            .transform(entities -> entities.stream()
                .map(OrderEntity::toDomain)
                .collect(Collectors.toList())
            );
    }

    @Override
    public Uni<Long> countByStatus(OrderStatus status) {
        return count("status = ?1", status.name());
    }

    /**
     * Finds orders with pagination.
     */
    public Uni<Tuple2<List<Order>, Long>> findOrdersWithPagination(int page, int size) {
        return Uni.combine()
            .all()
            .unis(
                find("order by createdAt desc")
                    .page(page, size)
                    .list()
                    .onItem()
                    .transform(entities -> entities.stream()
                        .map(OrderEntity::toDomain)
                        .collect(Collectors.toList())
                    ),
                count()
            )
            .asTuple();
    }

    /**
     * Finds orders by customer with pagination.
     */
    public Uni<Tuple2<List<Order>, Long>> findOrdersByCustomerWithPagination(
            UUID customerId, int page, int size) {
        return Uni.combine()
            .all()
            .unis(
                find("customerId = ?1 order by createdAt desc", customerId)
                    .page(page, size)
                    .list()
                    .onItem()
                    .transform(entities -> entities.stream()
                        .map(OrderEntity::toDomain)
                        .collect(Collectors.toList())
                    ),
                count("customerId = ?1", customerId)
            )
            .asTuple();
    }

    /**
     * Updates order status.
     */
    @WithTransaction
    public Uni<Order> updateStatus(UUID orderId, OrderStatus newStatus) {
        return findById(orderId)
            .chain(entity -> {
                if (entity == null) {
                    return Uni.createFrom().failure(
                        new IllegalArgumentException("Order not found: " + orderId)
                    );
                }
                entity.status = newStatus.name();
                entity.updatedAt = Instant.now();
                return persist(entity)
                    .onItem()
                    .transform(OrderEntity::toDomain);
            });
    }
}