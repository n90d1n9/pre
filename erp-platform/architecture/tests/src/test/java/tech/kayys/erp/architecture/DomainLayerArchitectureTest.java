package tech.kayys.erp.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit5.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Comprehensive architecture tests for the domain layer.
 * These tests enforce the hexagonal architecture principles.
 */
public class DomainLayerArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .importPackages("tech.kayys.erp");
    }

    // =========================================================================
    // DEPENDENCY DIRECTION RULES
    // =========================================================================

    @ArchTest
    static final ArchRule domainMustNotDependOnApplication =
            noClasses()
                    .that()
                    .resideInAnyPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..application..");

    @ArchTest
    static final ArchRule domainMustNotDependOnInfrastructure =
            noClasses()
                    .that()
                    .resideInAnyPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule domainMustNotDependOnInterfaces =
            noClasses()
                    .that()
                    .resideInAnyPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..interfaces..");

    @ArchTest
    static final ArchRule domainMustNotDependOnReactiveFrameworks =
            noClasses()
                    .that()
                    .resideInAnyPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "io.smallrye.mutiny..",
                            "io.reactivex..",
                            "reactor.core.."
                    );

    @ArchTest
    static final ArchRule domainMustNotDependOnQuarkus =
            noClasses()
                    .that()
                    .resideInAnyPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("io.quarkus..");

    // =========================================================================
    // FRAMEWORK-FREE RULES
    // =========================================================================

    @ArchTest
    static final ArchRule domainMustBeFrameworkFree =
            noClasses()
                    .that()
                    .resideInAnyPackage("..domain..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "io.quarkus..",
                            "io.smallrye..",
                            "org.hibernate..",
                            "jakarta.persistence..",
                            "org.eclipse.microprofile..",
                            "com.fasterxml.jackson.."
                    );

    // =========================================================================
    // APPLICATION LAYER RULES
    // =========================================================================

    @ArchTest
    static final ArchRule applicationMustNotDependOnInfrastructure =
            noClasses()
                    .that()
                    .resideInAnyPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule applicationMustNotDependOnInterfaces =
            noClasses()
                    .that()
                    .resideInAnyPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("..interfaces..");

    @ArchTest
    static final ArchRule applicationMustNotDependOnJpa =
            noClasses()
                    .that()
                    .resideInAnyPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "jakarta.persistence..",
                            "org.hibernate.."
                    );

    @ArchTest
    static final ArchRule applicationMustNotDependOnReactiveFrameworks =
            noClasses()
                    .that()
                    .resideInAnyPackage("..application..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "io.smallrye.mutiny..",
                            "io.reactivex..",
                            "reactor.core.."
                    );

    // =========================================================================
    // BOUNDED CONTEXT ISOLATION RULES
    // =========================================================================

    @ArchTest
    static final ArchRule catalogMustNotDependOnOtherContexts =
            noClasses()
                    .that()
                    .resideInAnyPackage("tech.kayys.erp.catalog..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage(
                            "tech.kayys.erp.sales..",
                            "tech.kayys.erp.inventory..",
                            "tech.kayys.erp.accounting..",
                            "tech.kayys.erp.crm.."
                    );

    // =========================================================================
    // NAMING CONVENTIONS
    // =========================================================================

    @ArchTest
    static final ArchRule aggregatesShouldEndWithEntity =
            classes()
                    .that()
                    .areAssignableTo(tech.kayys.erp.foundation.domain.AggregateRoot.class)
                    .should()
                    .haveSimpleNameNotEndingWith("Entity");

    @ArchTest
    static final ArchRule valueObjectsShouldBeFinal =
            classes()
                    .that()
                    .implement(tech.kayys.erp.foundation.domain.ValueObject.class)
                    .should()
                    .beFinal();

    @ArchTest
    static final ArchRule repositoriesShouldBeInterfaces =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Repository")
                    .should()
                    .beInterfaces();

    // =========================================================================
    // INFRASTRUCTURE RULES
    // =========================================================================

    @ArchTest
    static final ArchRule infrastructureShouldNotBeInDomain =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp..infrastructure..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAnyPackage("tech.kayys.erp..domain..")
                    .andShould()
                    .beAnnotatedWith(org.springframework.stereotype.Component.class);

    // =========================================================================
    // FOUNDATION RULES
    // =========================================================================

    @ArchTest
    static final ArchRule foundationShouldBeSmall =
            classes()
                    .that()
                    .resideInAPackage("tech.kayys.erp.foundation..")
                    .should()
                    .haveNameNotMatching(".*Product.*")
                    .andShould()
                    .haveNameNotMatching(".*Customer.*")
                    .andShould()
                    .haveNameNotMatching(".*Order.*")
                    .andShould()
                    .haveNameNotMatching(".*Invoice.*");

    // =========================================================================
    // COMPREHENSIVE LAYERED ARCHITECTURE
    // =========================================================================

    @ArchTest
    static final ArchRule layeredArchitectureRule =
            layeredArchitecture()
                    .consideringAllDependencies()
                    .layer("Domain").definedBy("..domain..")
                    .layer("Application").definedBy("..application..")
                    .layer("Infrastructure").definedBy("..infrastructure..")
                    .layer("Interfaces").definedBy("..interfaces..")
                    .layer("Foundation").definedBy("..foundation..")

                    // Domain layer - only depends on foundation
                    .whereLayer("Domain")
                    .mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Interfaces")
                    .whereLayer("Domain")
                    .mayNotAccessAnyLayer()
                    
                    // Application layer - depends on domain
                    .whereLayer("Application")
                    .mayOnlyAccessLayers("Domain", "Foundation")
                    
                    // Infrastructure layer - depends on all business layers
                    .whereLayer("Infrastructure")
                    .mayOnlyAccessLayers("Application", "Domain", "Foundation")
                    
                    // Interfaces layer - depends on application and domain
                    .whereLayer("Interfaces")
                    .mayOnlyAccessLayers("Application", "Domain", "Foundation")
                    .whereLayer("Foundation")
                    .mayNotAccessAnyLayer();
}
