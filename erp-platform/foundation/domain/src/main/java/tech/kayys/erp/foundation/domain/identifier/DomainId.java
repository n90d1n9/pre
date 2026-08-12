package tech.kayys.erp.foundation.domain.identifier;

/**
 * Marker abstraction for strongly typed domain identifiers.
 *
 * @param <T> the identifier value type
 */
public interface DomainId<T> {

    T value();

}
