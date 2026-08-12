package tech.kayys.erp.foundation.application.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * The application layer is allowed to be reactive (Mutiny) and to
 * depend on the domain module, but must never reach directly into a
 * concrete infrastructure technology. That belongs to the
 * adapters/infrastructure layer of each service, one level further
 * out: Infrastructure -> Application -> Domain, never the reverse and
 * never a skip.
 */
class ApplicationArchitectureTest {

    private static final String APPLICATION_PACKAGE =
            "tech.kayys.erp.foundation.application..";

    private static final JavaClasses APPLICATION_CLASSES =
            new ClassFileImporter()
                    .importPackages("tech.kayys.erp.foundation.application");

    private static final String[] FORBIDDEN_PACKAGES = {
            "io.quarkus..",
            "org.hibernate..",
            "jakarta.persistence..",
            "jakarta.ws.rs..",
            "org.apache.kafka..",
            "redis.clients..",
            "io.lettuce..",
            "com.fasterxml.jackson.."
    };

    @Test
    void applicationMustNotDependOnInfrastructure() {
        for (String forbiddenPackage : FORBIDDEN_PACKAGES) {
            noClasses()
                    .that().resideInAPackage(APPLICATION_PACKAGE)
                    .should().dependOnClassesThat().resideInAPackage(forbiddenPackage)
                    .because("infrastructure adapters (Postgres/Kafka/REST/...) "
                            + "belong outside the application layer")
                    .check(APPLICATION_CLASSES);
        }
    }

}
