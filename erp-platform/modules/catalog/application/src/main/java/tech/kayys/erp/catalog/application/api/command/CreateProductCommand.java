package tech.kayys.erp.catalog.application.api.command;

import tech.kayys.erp.catalog.domain.identifier.ProductId;
import tech.kayys.erp.foundation.application.Command;

import java.math.BigDecimal;

/**
 * Command to create a new product in the catalog.
 * This is the public API contract for creating products.
 */
public record CreateProductCommand(
        ProductId productId,
        String name,
        String description,
        BigDecimal price,
        String currencyCode,
        String sku
) implements Command<ProductId> {

    public CreateProductCommand {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be empty");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be empty");
        }
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU cannot be empty");
        }
    }

    /**
     * Builder for creating a CreateProductCommand with default values.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProductId productId;
        private String name;
        private String description;
        private BigDecimal price;
        private String currencyCode = "USD";
        private String sku;

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder sku(String sku) {
            this.sku = sku;
            return this;
        }

        public CreateProductCommand build() {
            if (productId == null) {
                productId = ProductId.generate();
            }
            return new CreateProductCommand(productId, name, description, price, currencyCode, sku);
        }
    }
}