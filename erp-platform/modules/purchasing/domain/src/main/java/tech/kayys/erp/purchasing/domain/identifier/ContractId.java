package tech.kayys.erp.purchasing.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for vendor contracts.
 */
public final class ContractId extends Identifier {

    private static final long serialVersionUID = 1L;

    private ContractId(String value) {
        super(value);
    }

    public static ContractId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Contract ID cannot be empty");
        }
        return new ContractId(value);
    }

    public static ContractId generate() {
        return new ContractId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContractId that = (ContractId) o;
        return Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public String toString() {
        return "ContractId{" + getValue() + "}";
    }
}
