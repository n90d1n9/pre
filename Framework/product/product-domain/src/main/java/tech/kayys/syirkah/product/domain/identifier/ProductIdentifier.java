package tech.kayys.syirkah.product.domain.identifier;

import java.util.Objects;

/**
 * One external identification of a product.
 *
 * The optional namespace scopes the value to the issuing system
 * (e.g. which supplier's ERP a SUPPLIER_CODE belongs to).
 */
public record ProductIdentifier(
        IdentifierType type,
        String value,
        String namespace
) {

    public ProductIdentifier {
        Objects.requireNonNull(type, "type cannot be null");

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Identifier value cannot be blank"
            );
        }

        value = value.trim();
        namespace = namespace == null || namespace.isBlank()
                ? null
                : namespace.trim();
    }

    public ProductIdentifier(IdentifierType type, String value) {
        this(type, value, null);
    }
}