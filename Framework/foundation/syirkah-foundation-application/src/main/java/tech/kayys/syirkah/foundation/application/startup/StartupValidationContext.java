package tech.kayys.syirkah.foundation.application.startup;

import tech.kayys.syirkah.foundation.application.config.ConfigurationSnapshot;
import tech.kayys.syirkah.foundation.application.config.profile.ResolvedProfile;
import tech.kayys.syirkah.foundation.application.runtime.ApplicationComponentRegistry;
import tech.kayys.syirkah.foundation.application.runtime.ApplicationRuntimeState;
import tech.kayys.syirkah.foundation.application.runtime.ModuleRegistry;
import tech.kayys.syirkah.foundation.application.runtime.RuntimeRegistry;

/**
 * Read-only context provided to startup checks (config01.md §P4-04 #6).
 */
public interface StartupValidationContext {

    ConfigurationSnapshot configuration();

    ResolvedProfile profile();

    RuntimeRegistry runtime();

    ModuleRegistry modules();

    ApplicationComponentRegistry components();

    ApplicationRuntimeState runtimeState();
}
