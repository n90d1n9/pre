package tech.kayys.syirkah.foundation.application.runtime;

import tech.kayys.syirkah.foundation.application.command.CommandBus;
import tech.kayys.syirkah.foundation.application.config.Configuration;
import tech.kayys.syirkah.foundation.application.config.RuntimeSettings;
import tech.kayys.syirkah.foundation.application.query.QueryBus;

/**
 * Immutable view of runtime-registered core components (config01.md §P4-01 #4).
 */
public interface RuntimeRegistry {

    CommandBus commandBus();

    QueryBus queryBus();

    Configuration configuration();

    RuntimeSettings settings();

    RuntimeDiagnostics diagnostics();

    RuntimeHealth health();
}
