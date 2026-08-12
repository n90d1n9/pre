package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.TicketId;

/**
 * Command to close a ticket.
 */
public record CloseTicketCommand(
        TicketId ticketId,
        String closedBy,
        String satisfactionRating,
        String satisfactionComment
) implements Command<TicketId> {

    public CloseTicketCommand {
        if (ticketId == null) {
            throw new IllegalArgumentException("Ticket ID cannot be null");
        }
        if (closedBy == null || closedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Closed by cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TicketId ticketId;
        private String closedBy;
        private String satisfactionRating;
        private String satisfactionComment;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder closedBy(String closedBy) {
            this.closedBy = closedBy;
            return this;
        }

        public Builder satisfactionRating(String satisfactionRating) {
            this.satisfactionRating = satisfactionRating;
            return this;
        }

        public Builder satisfactionComment(String satisfactionComment) {
            this.satisfactionComment = satisfactionComment;
            return this;
        }

        public CloseTicketCommand build() {
            return new CloseTicketCommand(ticketId, closedBy, satisfactionRating, satisfactionComment);
        }
    }
}