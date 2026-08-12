package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.TicketId;

/**
 * Command to add a comment to a ticket.
 */
public record AddTicketCommentCommand(
        TicketId ticketId,
        String author,
        String content,
        boolean internal
) implements Command<TicketId> {

    public AddTicketCommentCommand {
        if (ticketId == null) {
            throw new IllegalArgumentException("Ticket ID cannot be null");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TicketId ticketId;
        private String author;
        private String content;
        private boolean internal = false;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder internal(boolean internal) {
            this.internal = internal;
            return this;
        }

        public AddTicketCommentCommand build() {
            return new AddTicketCommentCommand(ticketId, author, content, internal);
        }
    }
}