package tech.kayys.erp.sales.application.port;

import tech.kayys.erp.foundation.domain.DomainEvent;
import tech.kayys.erp.sales.domain.model.Order;

import java.util.concurrent.CompletionStage;

/**
 * Port for publishing order events to other bounded contexts.
 */
public interface OrderEventPublisher {

    /**
     * Publishes all events for an order.
     */
    CompletionStage<Void> publishAllEvents(Order order);

    /**
     * Publishes a specific domain event.
     */
    CompletionStage<Void> publishEvent(DomainEvent event);
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
</modules>
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule salesDomainMustNotDependOnCatalog =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.catalog.domain..");

@ArchTest
static final ArchRule salesApplicationMustNotDependOnCatalog =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.application..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.catalog..")
                .andShould()
                .haveFullyQualifiedName("tech.kayys.erp.catalog.domain.model.Product");

@ArchTest
static final ArchRule salesApplicationMayUseCatalogPorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.application.port..")
                .should()
                .haveSimpleNameContaining("Port")
                .orShould()
                .haveSimpleNameContaining("Catalog");

@ArchTest
static final ArchRule salesDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.sales.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.sales.domain.model..",
                        "tech.kayys.erp.sales.domain.identifier..",
                        "tech.kayys.erp.sales.domain.valueobject..",
                        "tech.kayys.erp.sales.domain.event..",
                        "tech.kayys.erp.sales.domain.repository.."
                );