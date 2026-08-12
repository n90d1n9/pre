package tech.kayys.erp.foundation.application;

import java.io.Serializable;

/**
 * Marker interface for queries.
 * Queries represent read requests that do not modify system state.
 */
public interface Query<R> extends Serializable {
    // Generic query marker
}
