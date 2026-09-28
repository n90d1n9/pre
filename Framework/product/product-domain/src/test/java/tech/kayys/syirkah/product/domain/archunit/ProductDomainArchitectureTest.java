package tech.kayys.syirkah.product.domain.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import tech.kayys.syirkah.foundation.testing.architecture.ForbiddenDependencyRules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * The Product domain must stay pure Java and must never absorb the
 * commercial or operational capabilities that consume it
 * (product00.md: no pricing, promotion, tax, inventory, accounting
 * inside Product).
 */
class ProductDomainArchitectureTest {

    private static final String DOMAIN_PACKAGE =
            "tech.kayys.syirkah.product.domain..";

    private static final JavaClasses DOMAIN_CLASSES =
            new ClassFileImporter()
                    .importPackages("tech.kayys.syirkah.product.domain");

    @Test
    void domainMustNotDependOnFrameworksOrInfrastructure() {
        ForbiddenDependencyRules.checkNoDependencyOn(
                DOMAIN_CLASSES,
                DOMAIN_PACKAGE,
                ForbiddenDependencyRules.INFRASTRUCTURE_PACKAGES
        );
    }

    @Test
    void domainMustNotDependOnReactiveTypes() {
        ForbiddenDependencyRules.checkNoDependencyOn(
                DOMAIN_CLASSES,
                DOMAIN_PACKAGE,
                ForbiddenDependencyRules.REACTIVE_PACKAGES
        );
    }

    @Test
    void domainMustNotDependOnTheApplicationOrAdapterLayers() {
        noClasses()
                .that().resideInAPackage(DOMAIN_PACKAGE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.syirkah.product.application..",
                        "tech.kayys.syirkah.product.spi..",
                        "tech.kayys.syirkah.product.adapter.."
                )
                .because("dependency direction is Adapter -> Application -> Domain, never the reverse")
                .check(DOMAIN_CLASSES);
    }

    @Test
    void domainMustNotAbsorbCommercialOrOperationalCapabilities() {
        noClasses()
                .that().resideInAPackage(DOMAIN_PACKAGE)
                .should().dependOnClassesThat()
                .resideInAnyPackage(
                        "..commerce..",
                        "..pricing..",
                        "..promotion..",
                        "..inventory..",
                        "..accounting..",
                        "..retail..",
                        "..ecommerce.."
                )
                .because("Product is a reusable foundation: "
                        + "pricing, promotion, inventory and accounting "
                        + "consume Product IDs and events instead")
                .check(DOMAIN_CLASSES);
    }
}