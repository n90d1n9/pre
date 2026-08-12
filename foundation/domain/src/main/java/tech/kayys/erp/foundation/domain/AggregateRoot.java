package tech.kayys.erp.foundation.domain;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for Aggregate Roots in DDD.
 * Manages domain events and provides common aggregate functionality.
 * 
 * @param <ID> The type of the aggregate's identifier
 */
public abstract class AggregateRoot<ID extends Identifier<?>> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private ID id;
    private Instant createdAt;
    private Instant updatedAt;
    private int version;

    protected AggregateRoot(ID id) {
        this.id = id;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.version = 0;
    }

    protected AggregateRoot() {
        // For ORM/deserialization
    }

    public ID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public int getVersion() {
        return version;
    }

    protected void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    protected void incrementVersion() {
        this.version++;
    }

    /**
     * Records a domain event that occurred during the aggregate's lifecycle.
     * Events are dispatched after the aggregate is persisted.
     */
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * Clears all recorded domain events.
     * Should be called after events are dispatched.
     */
    public void clearEvents() {
        domainEvents.clear();
    }

    /**
     * Returns an immutable list of recorded domain events.
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
}
