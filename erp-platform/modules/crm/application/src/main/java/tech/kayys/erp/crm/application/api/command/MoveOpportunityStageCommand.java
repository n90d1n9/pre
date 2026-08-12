package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.OpportunityId;
import tech.kayys.erp.crm.domain.valueobject.OpportunityStage;

/**
 * Command to move an opportunity to a new stage.
 */
public record MoveOpportunityStageCommand(
        OpportunityId opportunityId,
        OpportunityStage newStage,
        String reason
) implements Command<OpportunityId> {

    public MoveOpportunityStageCommand {
        if (opportunityId == null) {
            throw new IllegalArgumentException("Opportunity ID cannot be null");
        }
        if (newStage == null) {
            throw new IllegalArgumentException("New stage cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OpportunityId opportunityId;
        private OpportunityStage newStage;
        private String reason;

        public Builder opportunityId(OpportunityId opportunityId) {
            this.opportunityId = opportunityId;
            return this;
        }

        public Builder newStage(OpportunityStage newStage) {
            this.newStage = newStage;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public MoveOpportunityStageCommand build() {
            return new MoveOpportunityStageCommand(opportunityId, newStage, reason);
        }
    }
}