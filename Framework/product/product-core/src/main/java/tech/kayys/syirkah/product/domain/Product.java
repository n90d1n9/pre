package tech.kayys.syirkah.product.domain;
import tech.kayys.syirkah.foundation.domain.entity.AbstractAggregateRoot;
import java.util.Objects;
public final class Product extends AbstractAggregateRoot<ProductId> {
    private ProductSku sku;
    private ProductName name;
    private ProductDescription description;
    private final ProductType type;
    private ProductStatus status;
    private Product(ProductId id, ProductSku sku, ProductName name, ProductDescription description, ProductType type) {
        super(id); this.sku = Objects.requireNonNull(sku); this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description); this.type = Objects.requireNonNull(type); this.status = ProductStatus.DRAFT;
    }
    public static Product create(ProductSku sku, ProductName name, ProductType type) {
        return new Product(ProductId.generate(), sku, name, ProductDescription.empty(), type);
    }
    public ProductSku sku() { return sku; }
    public ProductName name() { return name; }
    public ProductDescription description() { return description; }
    public ProductType type() { return type; }
    public ProductStatus status() { return status; }
    public void changeSku(ProductSku value) { ensureEditable(); sku = Objects.requireNonNull(value); touch(); }
    public void changeName(ProductName value) { ensureEditable(); name = Objects.requireNonNull(value); touch(); }
    public void changeDescription(ProductDescription value) { ensureEditable(); description = Objects.requireNonNull(value); touch(); }
    public void activate() { ensureNotDiscontinued(); status = ProductStatus.ACTIVE; touch(); }
    public void deactivate() { ensureNotDiscontinued(); status = ProductStatus.INACTIVE; touch(); }
    public void discontinue() { status = ProductStatus.DISCONTINUED; touch(); }
    private void ensureEditable() { ensureNotDiscontinued(); }
    private void ensureNotDiscontinued() { if (status == ProductStatus.DISCONTINUED) throw new IllegalStateException("Discontinued product cannot be modified"); }
    private void touch() { setUpdatedAt(java.time.Instant.now()); incrementVersion(); }
}
