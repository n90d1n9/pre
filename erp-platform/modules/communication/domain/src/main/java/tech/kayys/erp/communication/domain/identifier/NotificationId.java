package tech.kayys.erp.communication.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Notification identifier.
 */
public final class NotificationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public NotificationId(UUID value) {
        super(value);
    }

    public static NotificationId of(UUID value) {
        return new NotificationId(value);
    }

    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID());
    }

    public static NotificationId fromString(String value) {
        return new NotificationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "NotificationId{" + value + "}";
    }
}
