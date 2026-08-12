package tech.kayys.erp.purchasing.application.api;

import tech.kayys.erp.purchasing.application.api.command.*;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.util.concurrent.CompletionStage;

/**
 * Extended public API for contract commands.
 */
public interface ContractCommandService extends VendorCommandService {

    /**
     * Creates a contract from a template.
     */
    CompletionStage<ContractId> createContractFromTemplate(CreateFromTemplateCommand command);

    /**
     * Adds compliance record to a contract.
     */
    CompletionStage<ContractId> addComplianceRecord(AddComplianceRecordCommand command);

    /**
     * Adds performance metric to a contract.
     */
    CompletionStage<ContractId> addPerformanceMetric(AddPerformanceMetricCommand command);

    /**
     * Amends a contract.
     */
    CompletionStage<ContractId> amendContract(AmendContractCommand command);

    /**
     * Terminates a contract.
     */
    CompletionStage<ContractId> terminateContract(TerminateContractCommand command);

    /**
     * Processes auto-renewals.
     */
    CompletionStage<Integer> processAutoRenewals();
}