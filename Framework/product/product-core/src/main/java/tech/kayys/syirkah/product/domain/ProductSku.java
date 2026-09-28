package tech.kayys.syirkah.product.domain;
import java.util.Objects;
public record ProductSku(String value) {
    public ProductSku {
        Objects.requireNonNull(value, "SKU must not be null");
        value = value.trim();
        if (value.isEmpty() || value.length() > 128) throw new IllegalArgumentException("SKU must contain 1-128 characters");
    }
    public static ProductSku of(String value) { return new ProductSku(value); }
}
