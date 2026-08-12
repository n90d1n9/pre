package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.CreateVendorCommand;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.Vendor;
import tech.kayys.erp.purchasing.domain.repository.VendorRepository;
import tech.kayys.erp.purchasing.domain.valueobject.VendorStatus;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating vendors.
 */
@UseCase("Create a new vendor")
public class CreateVendorHandler implements CommandHandler<CreateVendorCommand, VendorId> {

    private final VendorRepository vendorRepository;

    @Inject
    public CreateVendorHandler(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Override
    public CompletionStage<VendorId> handle(CreateVendorCommand command) {
        // Check if vendor already exists
        return vendorRepository.existsByName(command.name())
            .thenCompose(exists -> {
                if (exists) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Vendor already exists: " + command.name())
                    );
                }

                // Create the vendor
                Vendor vendor = Vendor.create(
                    command.vendorId(),
                    command.name(),
                    command.vendorType(),
                    command.email(),
                    command.currencyCode()
                );

                // Set optional fields
                if (command.legalName() != null) {
                    vendor.setLegalName(command.legalName());
                }
                if (command.taxId() != null) {
                    vendor.setTaxId(command.taxId());
                }
                if (command.phone() != null) {
                    vendor.setPhone(command.phone());
                }
                if (command.address() != null) {
                    vendor.setAddress(command.address());
                }
                if (command.city() != null) {
                    vendor.setCity(command.city());
                }
                if (command.state() != null) {
                    vendor.setState(command.state());
                }
                if (command.postalCode() != null) {
                    vendor.setPostalCode(command.postalCode());
                }
                if (command.country() != null) {
                    vendor.setCountry(command.country());
                }
                if (command.website() != null) {
                    vendor.setWebsite(command.website());
                }
                if (command.contactPerson() != null) {
                    vendor.setContactPerson(command.contactPerson());
                }
                if (command.contactEmail() != null) {
                    vendor.setContactEmail(command.contactEmail());
                }
                if (command.contactPhone() != null) {
                    vendor.setContactPhone(command.contactPhone());
                }
                if (command.paymentTerms() != null) {
                    vendor.setPaymentTerms(command.paymentTerms());
                }
                if (command.shippingTerms() != null) {
                    vendor.setShippingTerms(command.shippingTerms());
                }
                if (command.notes() != null) {
                    vendor.setNotes(command.notes());
                }

                // Save the vendor
                return vendorRepository.save(vendor)
                    .thenApply(Vendor::getId);
            });
    }
}
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule purchasingDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory..",
                        "tech.kayys.erp.accounting.."
                );

@ArchTest
static final ArchRule purchasingApplicationMayUsePorts =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.application.port..")
                .should()
                .haveSimpleNameEndingWith("Port");

@ArchTest
static final ArchRule purchasingDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.purchasing.domain.model..",
                        "tech.kayys.erp.purchasing.domain.identifier..",
                        "tech.kayys.erp.purchasing.domain.valueobject..",
                        "tech.kayys.erp.purchasing.domain.event..",
                        "tech.kayys.erp.purchasing.domain.repository.."
                );

@ArchTest
static final ArchRule vendorContractStateMachine =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractStatus")
                .should()
                .haveOnlyFinalFields();

@ArchTest
static final ArchRule purchasingShouldNotDirectlyUseInventoryModel =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage("tech.kayys.erp.inventory.domain.model..");