package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.UUID;

public record CreatePortalTicketCommand(
        UUID customerId,
        String subject,
        String description,
        String priority,
        String category
) implements Command {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID customerId;
        private String subject;
        private String description;
        private String priority;
        private String category;

        public Builder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public Builder subject(String subject) { this.subject = subject; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder priority(String priority) { this.priority = priority; return this; }
        public Builder category(String category) { this.category = category; return this; }

        public CreatePortalTicketCommand build() {
            return new CreatePortalTicketCommand(customerId, subject, description, priority, category);
        }
    }
}
