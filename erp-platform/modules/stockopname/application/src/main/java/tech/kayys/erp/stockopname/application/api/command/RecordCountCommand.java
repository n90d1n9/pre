package tech.kayys.erp.stockopname.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.stockopname.domain.identifier.CountingSessionId;

/**
 * Command to record a count for an item.
 */
public record RecordCountCommand(
        CountingSessionId sessionId,
        String itemId,
        int countedQuantity,
        String countedBy,
        boolean isSecondCount
) implements Command<CountingSessionId> {

    public RecordCountCommand {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be empty");
        }
        if (countedQuantity < 0) {
            throw new IllegalArgumentException("Counted quantity cannot be negative");
        }
        if (countedBy == null || countedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Counted by cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CountingSessionId sessionId;
        private String itemId;
        private int countedQuantity;
        private String countedBy;
        private boolean isSecondCount = false;

        public Builder sessionId(CountingSessionId sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder itemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder countedQuantity(int countedQuantity) {
            this.countedQuantity = countedQuantity;
            return this;
        }

        public Builder countedBy(String countedBy) {
            this.countedBy = countedBy;
            return this;
        }

        public Builder secondCount(boolean isSecondCount) {
            this.isSecondCount = isSecondCount;
            return this;
        }

        public RecordCountCommand build() {
            return new RecordCountCommand(sessionId, itemId, countedQuantity, countedBy, isSecondCount);
        }
    }
}