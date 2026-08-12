package tech.kayys.erp.communication.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Communication template identifier.
 */
public final class TemplateId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TemplateId(UUID value) {
        super(value);
    }

    public static TemplateId of(UUID value) {
        return new TemplateId(value);
    }

    public static TemplateId generate() {
        return new TemplateId(UUID.randomUUID());
    }

    public static TemplateId fromString(String value) {
        return new TemplateId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TemplateId{" + value + "}";
    }
}
