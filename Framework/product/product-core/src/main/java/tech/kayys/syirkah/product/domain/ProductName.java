package tech.kayys.syirkah.product.domain;
import java.util.Objects;
public record ProductName(String value) {
    public ProductName {
        Objects.requireNonNull(value, "Product name must not be null");
        value = value.trim();
        if (value.isEmpty() || value.length() > 512) throw new IllegalArgumentException("Product name must contain 1-512 characters");
    }
    public static ProductName of(String value) { return new ProductName(value); }
}
