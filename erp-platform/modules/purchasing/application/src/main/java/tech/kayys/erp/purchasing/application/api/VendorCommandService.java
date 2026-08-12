package tech.kayys.erp.purchasing.application.api;

import tech.kayys.erp.purchasing.application.api.command.*;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for vendor and contract commands.
 */
public interface VendorCommandService {

    // ============ Vendor Commands ============

    /**
     * Creates a new vendor.
     */
    CompletionStage<VendorId> createVendor(CreateVendorCommand command);

    /**
     * Approves a vendor.
     */
    CompletionStage<VendorId> approveVendor(ApproveVendorCommand command);

    /**
     * Activates a vendor.
     */
    CompletionStage<VendorId> activateVendor(ActivateVendorCommand command);

    /**
     * Deactivates a vendor.
     */
    CompletionStage<VendorId> deactivateVendor(DeactivateVendorCommand command);

    /**
     * Blacklists a vendor.
     */
    CompletionStage<VendorId> blacklistVendor(BlacklistVendorCommand command);

    /**
     * Adds a contact to a vendor.
     */
    CompletionStage<VendorId> addVendorContact(AddVendorContactCommand command);

    /**
     * Removes a contact from a vendor.
     */
    CompletionStage<VendorId> removeVendorContact(RemoveVendorContactCommand command);

    /**
     * Records vendor performance.
     */
    CompletionStage<VendorId> recordVendorPerformance(RecordVendorPerformanceCommand command);

    // ============ Contract Commands ============

    /**
     * Creates a new vendor contract.
     */
    CompletionStage<ContractId> createContract(CreateContractCommand command);

    /**
     * Submits a contract for approval.
     */
    CompletionStage<ContractId> submitContract(SubmitContractCommand command);

    /**
     * Approves a contract.
     */
    CompletionStage<ContractId> approveContract(ApproveContractCommand command);

    /**
     * Activates a contract.
     */
    CompletionStage<ContractId> activateContract(ActivateContractCommand command);

    /**
     * Suspends a contract.
     */
    CompletionStage<ContractId> suspendContract(SuspendContractCommand command);

    /**
     * Terminates a contract.
     */
    CompletionStage<ContractId> terminateContract(TerminateContractCommand command);

    /**
     * Renews a contract.
     */
    CompletionStage<ContractId> renewContract(RenewContractCommand command);

    /**
     * Processes contract renewals.
     */
    CompletionStage<Integer> processContractRenewals();
}