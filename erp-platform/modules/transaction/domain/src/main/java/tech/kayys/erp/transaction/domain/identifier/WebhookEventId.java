package tech.kayys.erp.transaction.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

public final class WebhookEventId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WebhookEventId(UUID value) {
        super(value);
    }

    public static WebhookEventId of(UUID value) {
        return new WebhookEventId(value);
    }

    public static WebhookEventId generate() {
        return new WebhookEventId(UUID.randomUUID());
    }

    public static WebhookEventId fromString(String value) {
        return new WebhookEventId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WebhookEventId{" + value + "}";
    }
}