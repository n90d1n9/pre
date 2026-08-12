package tech.kayys.erp.crm.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.crm.domain.identifier.TicketId;
import tech.kayys.erp.crm.domain.valueobject.TicketPriority;

import java.util.UUID;

/**
 * Command to create a support ticket.
 */
public record CreateTicketCommand(
        TicketId ticketId,
        UUID customerId,
        String customerName,
        String subject,
        String description,
        TicketPriority priority,
        String category
) implements Command<TicketId> {

    public CreateTicketCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TicketId ticketId;
        private UUID customerId;
        private String customerName;
        private String subject;
        private String description;
        private TicketPriority priority = TicketPriority.MEDIUM;
        private String category;

        public Builder ticketId(TicketId ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder customerId(UUID customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder priority(TicketPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public CreateTicketCommand build() {
            if (ticketId == null) {
                ticketId = TicketId.generate();
            }
            return new CreateTicketCommand(
                ticketId, customerId, customerName,
                subject, description, priority, category
            );
        }
    }
}