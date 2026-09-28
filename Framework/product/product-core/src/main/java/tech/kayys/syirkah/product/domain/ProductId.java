package tech.kayys.syirkah.product.domain;
import tech.kayys.syirkah.foundation.domain.identifier.Identifier;
import java.util.UUID;
public final class ProductId extends Identifier<UUID> {
    private ProductId(UUID value) { super(value); }
    public static ProductId generate() { return new ProductId(UUID.randomUUID()); }
    public static ProductId of(UUID value) { return new ProductId(value); }
    public static ProductId of(String value) { return of(UUID.fromString(value)); }
}
