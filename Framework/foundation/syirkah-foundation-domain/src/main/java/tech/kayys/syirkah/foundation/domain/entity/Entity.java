package tech.kayys.syirkah.foundation.domain.entity;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

/**
 * An entity is defined by its identity rather than its attributes.
 *
 * @param <ID> strongly typed domain identifier
 */
public interface Entity<ID extends DomainId<?>> {

    ID id();

}
