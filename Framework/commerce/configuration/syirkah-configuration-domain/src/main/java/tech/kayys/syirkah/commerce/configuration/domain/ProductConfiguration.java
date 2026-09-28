package tech.kayys.syirkah.commerce.configuration.domain;

import tech.kayys.syirkah.foundation.domain.valueobject.ValueObject;
import tech.kayys.syirkah.product.domain.product.ProductId;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A customer's transient selection during a transaction.
 *
 * Mirrors the blueprint's {@code ProductConfiguration(productId, options)}.
 * This is deliberately NOT an aggregate: it is validated (by
 * {@link ConfigurationValidator}) and then consumed by pricing. It is
 * never persisted by this capability — see the {@code -spi} module docs.
 */
public record ProductConfiguration(
        ProductId productId,
        Map<String, SelectedOption> options
) implements ValueObject {

    public ProductConfiguration {
        Objects.requireNonNull(productId, "productId cannot be null");
        Objects.requireNonNull(options, "options cannot be null");
        options = Map.copyOf(options);
    }

    public static ProductConfiguration of(
            ProductId productId,
            List<SelectedOption> selections
    ) {
        Objects.requireNonNull(selections, "selections cannot be null");
        Map<String, SelectedOption> byGroup = selections.stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        SelectedOption::groupCode,
                        selection -> selection,
                        (first, second) -> {
                            throw new IllegalArgumentException(
                                    "Duplicate selection for group: " + first.groupCode());
                        }));
        return new ProductConfiguration(productId, byGroup);
    }

    public static ProductConfiguration empty(ProductId productId) {
        return new ProductConfiguration(productId, Map.of());
    }
}
