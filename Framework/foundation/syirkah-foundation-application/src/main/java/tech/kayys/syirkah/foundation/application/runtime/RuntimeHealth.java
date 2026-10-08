package tech.kayys.syirkah.foundation.application.runtime;

/**
 * Basic health status view exposed by the runtime registry.
 */
public interface RuntimeHealth {

    boolean isHealthy();

    String status();
}
