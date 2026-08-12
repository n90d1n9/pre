package tech.kayys.erp.foundation.domain;

import java.io.Serializable;

/**
 * Marker interface for Value Objects.
 * Value Objects are immutable, self-validating, and compared by their attributes.
 */
public interface ValueObject extends Serializable {
    
    /**
     * Validates the value object's state.
     * Throws IllegalArgumentException if invalid.
     */
    default void validate() {
        // Default: no validation
    }
}
