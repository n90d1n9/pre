package tech.kayys.erp.foundation.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 * Domain events represent facts that have occurred within the domain.
 */
public interface DomainEvent extends Serializable {
    
    UUID getEventId();
    
    String getEventType();
    
    Instant getOccurredAt();
    
    String getAggregateId();
    
    String getAggregateType();
    
    /**
     * Creates a builder for constructing domain events.
     */
    static <E extends DomainEvent, A extends AggregateRoot<?>> Builder<E> builder(
            DomainEventFactory<E, A> factory) {
        return new Builder<>(factory);
    }
    
    /**
     * Builder for domain events.
     */
    class Builder<E extends DomainEvent, A extends AggregateRoot<?>> {
        private final DomainEventFactory<E, A> factory;
        private UUID eventId;
        private Instant occurredAt;
        private A aggregate;

        public Builder(DomainEventFactory<E, A> factory) {
            this.factory = factory;
        }

        public Builder<E, A> eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder<E, A> occurredAt(Instant occurredAt) {
            this.occurredAt = occurredAt;
            return this;
        }

        public Builder<E, A> fromAggregate(A aggregate) {
            this.aggregate = aggregate;
            return this;
        }

        public E build() {
            return factory.create(
                eventId != null ? eventId : UUID.randomUUID(),
                occurredAt != null ? occurredAt : Instant.now(),
                aggregate
            );
        }
    }
    
    @FunctionalInterface
    interface DomainEventFactory<E extends DomainEvent, A extends AggregateRoot<?>> {
        E create(UUID eventId, Instant occurredAt, A aggregate);
    }
}
