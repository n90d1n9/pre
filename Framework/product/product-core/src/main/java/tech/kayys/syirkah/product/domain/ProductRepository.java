package tech.kayys.syirkah.product.domain;
import tech.kayys.syirkah.foundation.domain.repository.Repository;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
public interface ProductRepository extends Repository<Product, ProductId> {
    CompletionStage<Optional<Product>> findBySku(ProductSku sku);
    CompletionStage<Boolean> existsBySku(ProductSku sku);
}
