package tech.kayys.erp.communication.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Message identifier.
 */
public final class MessageId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public MessageId(UUID value) {
        super(value);
    }

    public static MessageId of(UUID value) {
        return new MessageId(value);
    }

    public static MessageId generate() {
        return new MessageId(UUID.randomUUID());
    }

    public static MessageId fromString(String value) {
        return new MessageId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "MessageId{" + value + "}";
    }
}
