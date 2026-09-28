package tech.kayys.syirkah.foundation.domain.identifier;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Base class for domain identifiers.
 * Ensures type-safety, implements DomainId<T> and Serializable.
 *
 * @param <T> The type of the identifier value
 */
public abstract class Identifier<T> implements DomainId<T>, Serializable {

    private static final long serialVersionUID = 1L;

    protected final T value;

    protected Identifier(T value) {
        this.value = Objects.requireNonNull(value, "Identifier value cannot be null");
    }

    @Override
    public T value() {
        return value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier<?> that = (Identifier<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }

    /**
     * Convenience method for generating random UUID-based identifiers.
     */
    public static <I extends Identifier<UUID>> I randomUuid(IdentifierFactory<UUID, I> factory) {
        return factory.create(UUID.randomUUID());
    }

    /**
     * Factory interface for creating typed identifiers from raw values.
     */
    @FunctionalInterface
    public interface IdentifierFactory<V, I extends Identifier<V>> {
        I create(V value);
    }
}
