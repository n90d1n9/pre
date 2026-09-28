package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class EmailMessageId extends Identifier<UUID> {

    public EmailMessageId(UUID value) {
        super(value);
    }

    public static EmailMessageId generate() {
        return new EmailMessageId(UUID.randomUUID());
    }

    public static EmailMessageId of(UUID value) {
        return new EmailMessageId(value);
    }

    public static EmailMessageId of(String value) {
        return new EmailMessageId(UUID.fromString(value));
    }
}
