package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.UUID;

public record RegisterPortalUserCommand(
        UUID customerId,
        String customerName,
        String email,
        String username,
        String password
) implements Command {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID customerId;
        private String customerName;
        private String email;
        private String username;
        private String password;

        public Builder customerId(UUID customerId) { this.customerId = customerId; return this; }
        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder password(String password) { this.password = password; return this; }

        public RegisterPortalUserCommand build() {
            return new RegisterPortalUserCommand(customerId, customerName, email, username, password);
        }
    }
}
