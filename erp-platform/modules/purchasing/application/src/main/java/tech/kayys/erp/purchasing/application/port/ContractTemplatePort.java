package tech.kayys.erp.purchasing.application.port;

import tech.kayys.erp.purchasing.domain.valueobject.ContractTemplate;

import java.util.concurrent.CompletionStage;

/**
 * Port for contract template operations.
 */
public interface ContractTemplatePort {

    /**
     * Gets a contract template by ID.
     */
    CompletionStage<ContractTemplate> getTemplate(String templateId);

    /**
     * Gets all active templates.
     */
    CompletionStage<List<ContractTemplate>> getActiveTemplates();

    /**
     * Gets templates by contract type.
     */
    CompletionStage<List<ContractTemplate>> getTemplatesByType(String contractType);

    /**
     * Renders a template with data.
     */
    default CompletionStage<String> renderTemplate(String templateId, String data) {
        return getTemplate(templateId)
            .thenApply(template -> template.renderWithData(data));
    }
}
package tech.kayys.erp.purchasing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;
import tech.kayys.erp.purchasing.domain.model.VendorContract;
import tech.kayys.erp.purchasing.domain.valueobject.ContractStatus;
import tech.kayys.erp.purchasing.domain.valueobject.ContractType;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Extended repository for VendorContract aggregates.
 */
public interface VendorContractRepository extends Repository<VendorContract, ContractId> {

    /**
     * Finds contracts by vendor.
     */
    CompletionStage<List<VendorContract>> findByVendorId(VendorId vendorId);

    /**
     * Finds contracts by status.
     */
    CompletionStage<List<VendorContract>> findByStatus(ContractStatus status);

    /**
     * Finds active contracts.
     */
    default CompletionStage<List<VendorContract>> findActiveContracts() {
        return findByStatus(ContractStatus.ACTIVE);
    }

    /**
     * Finds contracts by type.
     */
    CompletionStage<List<VendorContract>> findByType(ContractType type);

    /**
     * Finds contracts expiring between two dates.
     */
    CompletionStage<List<VendorContract>> findExpiringBetween(Instant start, Instant end);

    /**
     * Finds contracts needing renewal (expiring within 30 days).
     */
    default CompletionStage<List<VendorContract>> findContractsNeedingRenewal() {
        Instant now = Instant.now();
        Instant threshold = now.plusSeconds(30L * 24L * 60L * 60L);
        return findExpiringBetween(now, threshold);
    }

    /**
     * Finds contracts with compliance issues.
     */
    CompletionStage<List<VendorContract>> findContractsWithComplianceIssues();

    /**
     * Finds contracts with performance issues.
     */
    CompletionStage<List<VendorContract>> findContractsWithPerformanceIssues();

    /**
     * Finds contracts by vendor and status.
     */
    CompletionStage<List<VendorContract>> findByVendorAndStatus(
        VendorId vendorId, 
        ContractStatus status
    );

    /**
     * Finds contracts for renewal processing.
     */
    CompletionStage<List<VendorContract>> findContractsForRenewal(Instant currentDate);

    /**
     * Gets the total contract value by vendor.
     */
    CompletionStage<Money> getTotalContractValueByVendor(VendorId vendorId);
}
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule contractsShouldHaveCorrectNaming =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.model..")
                .and()
                .haveSimpleNameContaining("Contract")
                .should()
                .haveSimpleNameEndingWith("Contract")
                .orShould()
                .haveSimpleNameEndingWith("VendorContract");

@ArchTest
static final ArchRule contractStatusTransitionsShouldBeEnforced =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractStatus")
                .should()
                .haveMethod("canTransitionTo");

@ArchTest
static final ArchRule contractTemplatesShouldBeImmutable =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractTemplate")
                .should()
                .beFinal()
                .andShould()
                .haveOnlyFinalFields();

@ArchTest
static final ArchRule contractAmendmentsShouldBeImmutable =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.purchasing.domain.valueobject..")
                .and()
                .haveSimpleName("ContractAmendment")
                .should()
                .beFinal()
                .andShould()
                .haveOnlyFinalFields();