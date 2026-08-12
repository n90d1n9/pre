package tech.kayys.erp.subscription.application.port;

import tech.kayys.erp.subscription.domain.model.Subscription;
import tech.kayys.erp.subscription.domain.valueobject.Money;

import java.util.concurrent.CompletionStage;

/**
 * Port for processing payments.
 */
public interface PaymentProcessorPort {

    /**
     * Processes a payment for a subscription.
     */
    CompletionStage<PaymentResult> processPayment(Subscription subscription, Money amount);

    /**
     * Refunds a payment.
     */
    CompletionStage<RefundResult> refundPayment(String transactionId, Money amount);

    record PaymentResult(
        boolean success,
        String transactionId,
        String message
    ) {}

    record RefundResult(
        boolean success,
        String refundId,
        String message
    ) {}
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

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>
</modules>
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule subscriptionDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory..",
                        "tech.kayys.erp.accounting.."
                );

@ArchTest
static final ArchRule subscriptionApplicationMayUsePorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.application.port..")
                .should()
                .haveSimpleNameEndingWith("Port")
                .orShould()
                .haveSimpleNameEndingWith("Provider")
                .orShould()
                .haveSimpleNameEndingWith("Processor");

@ArchTest
static final ArchRule subscriptionDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.subscription.domain.model..",
                        "tech.kayys.erp.subscription.domain.identifier..",
                        "tech.kayys.erp.subscription.domain.valueobject..",
                        "tech.kayys.erp.subscription.domain.event..",
                        "tech.kayys.erp.subscription.domain.repository.."
                );

@ArchTest
static final ArchRule subscriptionStatusStateMachine =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.subscription.domain.valueobject..")
                .and()
                .haveSimpleName("SubscriptionStatus")
                .should()
                .haveOnlyFinalFields();