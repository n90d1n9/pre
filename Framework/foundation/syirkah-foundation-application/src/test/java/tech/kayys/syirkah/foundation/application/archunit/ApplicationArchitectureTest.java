package tech.kayys.syirkah.foundation.application.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import tech.kayys.syirkah.foundation.testing.architecture.ForbiddenDependencyRules;

/**
 * The application layer is allowed to be reactive (Mutiny) and to
 * depend on the domain module, but must never reach directly into a
 * concrete infrastructure technology. That belongs to the
 * adapters/infrastructure layer of each service, one level further
 * out: Infrastructure -> Application -> Domain, never the reverse and
 * never a skip.
 *
 * Uses the shared {@link ForbiddenDependencyRules} from
 * syirkah-foundation-testing instead of a locally duplicated package
 * list - every downstream bounded-context module's application layer
 * should follow the same pattern.
 */
class ApplicationArchitectureTest {

        private static final String APPLICATION_PACKAGE = "tech.kayys.syirkah.foundation.application..";

        private static final JavaClasses APPLICATION_CLASSES = new ClassFileImporter()
                        .importPackages("tech.kayys.syirkah.foundation.application");

        @Test
        void applicationMustNotDependOnInfrastructure() {
                ForbiddenDependencyRules.checkNoDependencyOn(
                                APPLICATION_CLASSES,
                                APPLICATION_PACKAGE,
                                ForbiddenDependencyRules.INFRASTRUCTURE_PACKAGES);
        }

}
