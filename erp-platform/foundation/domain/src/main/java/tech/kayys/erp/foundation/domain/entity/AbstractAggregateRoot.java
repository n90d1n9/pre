package tech.kayys.erp.foundation.domain.entity;

import tech.kayys.erp.foundation.domain.event.DomainEvent;
import tech.kayys.erp.foundation.domain.identifier.DomainId;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for aggregate roots.
 *
 * @param <ID> aggregate identifier
 */
public abstract class AbstractAggregateRoot<ID extends DomainId<?>>
        implements AggregateRoot<ID> {

    private final List<DomainEvent> domainEvents =
            new ArrayList<>();

    /**
     * Registers a domain event raised by this aggregate.
     */
    protected final void raise(DomainEvent event) {
        if (event == null) {
            throw new IllegalArgumentException(
                    "Domain event cannot be null"
            );
        }

        domainEvents.add(event);
    }

    /**
     * Returns all pending events and clears the internal collection.
     */
    @Override
    public final List<DomainEvent> pullDomainEvents() {
        if (domainEvents.isEmpty()) {
            return List.of();
        }

        final var events = List.copyOf(domainEvents);
        domainEvents.clear();

        return events;
    }

}
