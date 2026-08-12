package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;

/**
 * Command to renew a contract.
 */
public record RenewContractCommand(
        ContractId contractId,
        Instant newExpirationDate,
        String notes
) implements Command<ContractId> {

    public RenewContractCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (newExpirationDate == null) {
            throw new IllegalArgumentException("New expiration date is required");
        }
        if (newExpirationDate.isBefore(Instant.now())) {
            throw new IllegalArgumentException("New expiration date must be in the future");
        }
    }
}