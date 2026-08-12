package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.ContractId;

import java.time.Instant;

/**
 * Command to add a compliance record to a contract.
 */
public record AddComplianceRecordCommand(
        ContractId contractId,
        String complianceType,
        String description,
        Instant reviewDate,
        Instant nextReviewDate,
        String reviewer,
        String findings,
        boolean compliant,
        String recommendations
) implements Command<ContractId> {

    public AddComplianceRecordCommand {
        if (contractId == null) {
            throw new IllegalArgumentException("Contract ID cannot be null");
        }
        if (complianceType == null || complianceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Compliance type cannot be empty");
        }
        if (reviewDate == null) {
            throw new IllegalArgumentException("Review date is required");
        }
        if (reviewer == null || reviewer.trim().isEmpty()) {
            throw new IllegalArgumentException("Reviewer cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContractId contractId;
        private String complianceType;
        private String description;
        private Instant reviewDate;
        private Instant nextReviewDate;
        private String reviewer;
        private String findings;
        private boolean compliant = true;
        private String recommendations;

        public Builder contractId(ContractId contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder complianceType(String complianceType) {
            this.complianceType = complianceType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder reviewDate(Instant reviewDate) {
            this.reviewDate = reviewDate;
            return this;
        }

        public Builder nextReviewDate(Instant nextReviewDate) {
            this.nextReviewDate = nextReviewDate;
            return this;
        }

        public Builder reviewer(String reviewer) {
            this.reviewer = reviewer;
            return this;
        }

        public Builder findings(String findings) {
            this.findings = findings;
            return this;
        }

        public Builder compliant(boolean compliant) {
            this.compliant = compliant;
            return this;
        }

        public Builder recommendations(String recommendations) {
            this.recommendations = recommendations;
            return this;
        }

        public AddComplianceRecordCommand build() {
            if (reviewDate == null) {
                reviewDate = Instant.now();
            }
            return new AddComplianceRecordCommand(
                contractId, complianceType, description, reviewDate,
                nextReviewDate, reviewer, findings, compliant, recommendations
            );
        }
    }
}