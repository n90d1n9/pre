package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;

/**
 * Command to amend a contract.
 */
public record AmendContractCommand(
        ContractId contractId,
        String amendmentNumber,
        String description,
        String changeType,
        String beforeChange,
        String afterChange,
        Instant effectiveDate,
        String approvedBy,
        String notes
) implements Command<ContractId> {

    public AmendContractCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (changeType == null || changeType.trim().isEmpty()) {
            throw new IllegalArgumentException("Change type cannot be empty");
        }
        if (effectiveDate == null) {
            throw new IllegalArgumentException("Effective date is required");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractId contractId;
        private String amendmentNumber;
        private String description;
        private String changeType;
        private String beforeChange;
        private String afterChange;
        private Instant effectiveDate;
        private String approvedBy;
        private String notes;

        public Builder contractId(ContractId contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder amendmentNumber(String amendmentNumber) {
            this.amendmentNumber = amendmentNumber;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder changeType(String changeType) {
            this.changeType = changeType;
            return this;
        }

        public Builder beforeChange(String beforeChange) {
            this.beforeChange = beforeChange;
            return this;
        }

        public Builder afterChange(String afterChange) {
            this.afterChange = afterChange;
            return this;
        }

        public Builder effectiveDate(Instant effectiveDate) {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public Builder approvedBy(String approvedBy) {
            this.approvedBy = approvedBy;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public AmendContractCommand build() {
            if (amendmentNumber == null) {
                amendmentNumber = "AMD-" + System.currentTimeMillis();
            }
            if (effectiveDate == null) {
                effectiveDate = Instant.now();
            }
            return new AmendContractCommand(
                contractId, amendmentNumber, description, changeType,
                beforeChange, afterChange, effectiveDate, approvedBy, notes
            );
        }
    }
}