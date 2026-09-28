package tech.kayys.syirkah.product.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ProductTest {
    @Test void supportsLifecycle() {
        Product product = Product.create(ProductSku.of("SKU-1"), ProductName.of("Coffee"), ProductType.PHYSICAL);
        assertEquals(ProductStatus.DRAFT, product.status());
        product.activate(); assertEquals(ProductStatus.ACTIVE, product.status());
        product.discontinue(); assertThrows(IllegalStateException.class, product::activate);
    }
}
