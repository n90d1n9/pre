package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

/**
 * Command to approve a contract.
 */
public record ApproveContractCommand(
        ContractId contractId,
        String approvedBy
) implements Command<ContractId> {

    public ApproveContractCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by is required");
        }
    }
}