package tech.kayys.syirkah.product.domain;
public record ProductDescription(String value) {
    public ProductDescription {
        value = value == null ? "" : value.trim();
        if (value.length() > 10_000) throw new IllegalArgumentException("Product description must not exceed 10000 characters");
    }
    public static ProductDescription empty() { return new ProductDescription(""); }
}
