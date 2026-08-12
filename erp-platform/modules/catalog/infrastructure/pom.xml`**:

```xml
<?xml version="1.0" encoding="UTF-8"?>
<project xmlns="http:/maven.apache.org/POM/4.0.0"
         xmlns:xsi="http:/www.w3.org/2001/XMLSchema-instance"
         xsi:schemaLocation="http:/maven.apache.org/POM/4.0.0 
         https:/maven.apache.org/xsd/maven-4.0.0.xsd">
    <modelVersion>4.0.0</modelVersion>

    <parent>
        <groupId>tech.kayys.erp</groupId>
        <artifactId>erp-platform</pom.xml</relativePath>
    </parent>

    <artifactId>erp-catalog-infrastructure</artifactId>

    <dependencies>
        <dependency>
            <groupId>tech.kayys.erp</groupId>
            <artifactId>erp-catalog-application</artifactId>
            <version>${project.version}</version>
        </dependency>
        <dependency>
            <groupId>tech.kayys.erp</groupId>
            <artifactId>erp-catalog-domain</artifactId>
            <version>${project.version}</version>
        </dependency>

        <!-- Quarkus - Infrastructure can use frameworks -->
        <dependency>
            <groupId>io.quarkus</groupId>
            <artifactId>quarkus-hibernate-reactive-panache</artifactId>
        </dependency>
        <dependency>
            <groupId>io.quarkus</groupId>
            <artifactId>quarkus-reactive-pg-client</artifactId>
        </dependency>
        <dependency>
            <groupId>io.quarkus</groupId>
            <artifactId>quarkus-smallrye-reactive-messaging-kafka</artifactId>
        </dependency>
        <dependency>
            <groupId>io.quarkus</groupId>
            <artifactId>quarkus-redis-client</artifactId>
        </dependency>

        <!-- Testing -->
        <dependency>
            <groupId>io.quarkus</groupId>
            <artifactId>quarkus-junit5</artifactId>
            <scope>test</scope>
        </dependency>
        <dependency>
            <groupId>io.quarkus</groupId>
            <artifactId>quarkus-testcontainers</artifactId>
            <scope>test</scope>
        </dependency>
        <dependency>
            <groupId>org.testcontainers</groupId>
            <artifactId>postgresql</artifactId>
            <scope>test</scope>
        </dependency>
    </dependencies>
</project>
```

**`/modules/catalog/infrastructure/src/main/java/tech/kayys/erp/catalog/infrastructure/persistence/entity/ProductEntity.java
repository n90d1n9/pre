package tech.kayys.erp.catalog.infrastructure.persistence.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for Product persistence.
 * This is separate from the domain model to maintain clean architecture.
 */
@Entity
@Table(name = "products")
public class ProductEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id", columnDefinition = "UUID")
    public UUID id;

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Column(name = "description", length = 2000)
    public String description;

    @Column(name = "price", precision = 19, scale = 2, nullable = false)
    public BigDecimal price;

    @Column(name = "currency", length = 3, nullable = false)
    public String currency;

    @Column(name = "sku", unique = true, nullable = false, length = 50)
    public String sku;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    public ProductStatusEntity status;

    @Column(name = "stock_level", nullable = false)
    public int stockLevel;

    @Column(name = "active", nullable = false)
    public boolean active;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;

    @Column(name = "version", nullable = false)
    @Version
    public int version;

    public ProductEntity() {
        // Default constructor for JPA
    }

    public enum ProductStatusEntity {
        DRAFT, ACTIVE, INACTIVE, DISCONTINUED
    }
}