package tech.kayys.erp.catalog.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.catalog.domain.model.Product;
import tech.kayys.erp.catalog.domain.valueobject.Money;
import tech.kayys.erp.catalog.domain.valueobject.ProductStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Product entity for Catalog context.
 * Maps domain Product to database.
 */
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

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
    public String status;

    @Column(name = "stock_level", nullable = false)
    public int stockLevel;

    @Column(name = "active", nullable = false)
    public boolean active;

    @Column(name = "category_id", columnDefinition = "UUID")
    public UUID categoryId;

    @Column(name = "brand", length = 100)
    public String brand;

    @Column(name = "manufacturer", length = 100)
    public String manufacturer;

    @Column(name = "upc", length = 20)
    public String upc;

    @Column(name = "ean", length = 20)
    public String ean;

    @Column(name = "mpn", length = 50)
    public String mpn;

    @Column(name = "weight")
    public Double weight;

    @Column(name = "weight_unit", length = 5)
    public String weightUnit;

    @Column(name = "taxable")
    public boolean taxable = true;

    @Column(name = "tax_code", length = 20)
    public String taxCode;

    @Column(name = "shippable")
    public boolean shippable = true;

    @Column(name = "min_order_quantity")
    public int minOrderQuantity = 1;

    @Column(name = "max_order_quantity")
    public int maxOrderQuantity = 100;

    @Column(name = "seo_title", length = 200)
    public String seoTitle;

    @Column(name = "seo_description", length = 500)
    public String seoDescription;

    @Column(name = "meta_keywords", length = 500)
    public String metaKeywords;

    public Product toDomain() {
        Product product = Product.create(
            ProductId.of(id),
            name,
            description,
            Money.of(price, currency),
            sku
        );
        // Additional fields would be set using reflection or builder pattern
        // For simplicity, this is a basic mapping
        return product;
    }

    public static ProductEntity fromDomain(Product product) {
        ProductEntity entity = new ProductEntity();
        entity.id = product.getId().getValue();
        entity.name = product.getName();
        entity.description = product.getDescription();
        entity.price = product.getPrice().getAmount();
        entity.currency = product.getPrice().getCurrency().getCurrencyCode();
        entity.sku = product.getSku();
        entity.status = product.getStatus().name();
        entity.stockLevel = product.getStockLevel();
        entity.active = product.isActive();
        entity.createdAt = product.getCreatedAt();
        entity.updatedAt = product.getUpdatedAt();
        entity.version = product.getVersion();
        return entity;
    }
}