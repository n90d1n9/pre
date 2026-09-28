package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class EmailTemplateId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public EmailTemplateId(UUID value) {
        super(value);
    }

    public static EmailTemplateId of(UUID value) {
        return new EmailTemplateId(value);
    }

    public static EmailTemplateId generate() {
        return new EmailTemplateId(UUID.randomUUID());
    }

    public static EmailTemplateId fromString(String value) {
        return new EmailTemplateId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "EmailTemplateId{" + value + "}";
    }
}
