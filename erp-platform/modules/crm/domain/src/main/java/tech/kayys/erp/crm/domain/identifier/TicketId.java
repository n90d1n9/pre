package tech.kayys.erp.crm.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Support ticket identifier.
 */
public final class TicketId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TicketId(UUID value) {
        super(value);
    }

    public static TicketId of(UUID value) {
        return new TicketId(value);
    }

    public static TicketId generate() {
        return new TicketId(UUID.randomUUID());
    }

    public static TicketId fromString(String value) {
        return new TicketId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TicketId{" + value + "}";
    }
}