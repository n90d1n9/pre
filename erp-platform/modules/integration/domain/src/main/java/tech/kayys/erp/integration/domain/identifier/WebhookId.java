package tech.kayys.erp.integration.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Webhook identifier.
 */
public final class WebhookId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WebhookId(UUID value) {
        super(value);
    }

    public static WebhookId of(UUID value) {
        return new WebhookId(value);
    }

    public static WebhookId generate() {
        return new WebhookId(UUID.randomUUID());
    }

    public static WebhookId fromString(String value) {
        return new WebhookId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WebhookId{" + value + "}";
    }
}