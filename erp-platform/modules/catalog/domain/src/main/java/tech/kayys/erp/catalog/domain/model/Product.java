package tech.kayys.erp.catalog.domain.model;

import tech.kayys.erp.catalog.domain.event.ProductCreated;
import tech.kayys.erp.catalog.domain.event.ProductPriceChanged;
import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.catalog.domain.valueobject.Money;
import tech.kayys.erp.catalog.domain.valueobject.ProductStatus;
import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.foundation.domain.DomainEvent;

import java.time.Instant;

/**
 * Product aggregate root in the Catalog bounded context.
 * Represents a product that can be sold through the ERP system.
 */
public final class Product extends AggregateRoot<ProductId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private Money price;
    private ProductStatus status;
    private String sku;
    private int stockLevel;
    private boolean active;

    private Product(ProductId id) {
        super(id);
        this.status = ProductStatus.DRAFT;
        this.active = true;
        this.stockLevel = 0;
    }

    // Private constructor for ORM/deserialization
    private Product() {
        super();
    }

    /**
     * Factory method to create a new Product.
     * This is the only way to create a Product, ensuring business invariants.
     */
    public static Product create(
            ProductId id,
            String name,
            String description,
            Money price,
            String sku
    ) {
        Product product = new Product(id);
        product.name = name;
        product.description = description;
        product.price = price;
        product.sku = sku;
        product.status = ProductStatus.DRAFT;
        product.active = true;
        product.stockLevel = 0;
        
        // Register domain event
        product.registerEvent(new ProductCreated(product));
        
        return product;
    }

    /**
     * Business method: Activate the product for sale.
     */
    public void activate() {
        if (this.status == ProductStatus.ACTIVE) {
            return;
        }
        if (price == null || price.getAmount().signum() <= 0) {
            throw new IllegalStateException("Cannot activate product without valid price");
        }
        this.status = ProductStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Business method: Deactivate the product.
     */
    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Business method: Update the product's price.
     */
    public void changePrice(Money newPrice) {
        if (newPrice == null || newPrice.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        
        Money oldPrice = this.price;
        this.price = newPrice;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        // Register domain event
        registerEvent(new ProductPriceChanged(this, oldPrice, newPrice));
    }

    /**
     * Business method: Update stock level.
     */
    public void adjustStock(int quantity) {
        if (this.stockLevel + quantity < 0) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        this.stockLevel += quantity;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Money getPrice() { return price; }
    public ProductStatus getStatus() { return status; }
    public String getSku() { return sku; }
    public int getStockLevel() { return stockLevel; }
    public boolean isActive() { return active && status == ProductStatus.ACTIVE; }

    /**
     * Checks if the product is available for sale.
     */
    public boolean isAvailable() {
        return isActive() && stockLevel > 0;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", status=" + status +
                '}';
    }
}
