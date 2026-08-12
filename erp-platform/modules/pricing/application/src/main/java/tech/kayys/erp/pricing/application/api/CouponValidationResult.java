package tech.kayys.erp.pricing.application.api;

import tech.kayys.erp.pricing.domain.model.PricingRule;

/**
 * Result of coupon validation.
 */
public record CouponValidationResult(
        boolean valid,
        String message,
        PricingRule rule
) {
    public CouponValidationResult(boolean valid, String message) {
        this(valid, message, null);
    }

    public boolean isValid() {
        return valid;
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>
</modules>
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule pricingDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory.."
                );

@ArchTest
static final ArchRule pricingApplicationMayUsePorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.application.port..")
                .should()
                .haveSimpleNameEndingWith("Port")
                .orShould()
                .haveSimpleNameEndingWith("Provider");

@ArchTest
static final ArchRule pricingDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.pricing.domain.model..",
                        "tech.kayys.erp.pricing.domain.identifier..",
                        "tech.kayys.erp.pricing.domain.valueobject..",
                        "tech.kayys.erp.pricing.domain.repository.."
                );

@ArchTest
static final ArchRule pricingApplicationShouldNotDependOnCatalogDirectly =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.pricing.application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.catalog..")
                .andShould()
                .haveFullyQualifiedName("tech.kayys.erp.catalog.domain.model.Product");