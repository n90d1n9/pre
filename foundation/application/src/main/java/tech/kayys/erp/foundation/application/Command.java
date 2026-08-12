package tech.kayys.erp.foundation.application;

import java.io.Serializable;

/**
 * Marker interface for commands.
 * Commands represent intentions to change the system state.
 */
public interface Command<R> extends Serializable {
    // Generic command marker
}
