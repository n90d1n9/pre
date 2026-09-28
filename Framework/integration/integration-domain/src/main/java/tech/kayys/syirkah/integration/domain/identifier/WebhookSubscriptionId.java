package tech.kayys.syirkah.integration.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/** Identifies a webhook subscription belonging to an external system. */
public final class WebhookSubscriptionId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public WebhookSubscriptionId(UUID value) {
        super(value);
    }

    public static WebhookSubscriptionId of(UUID value) {
        return new WebhookSubscriptionId(value);
    }

    public static WebhookSubscriptionId generate() {
        return new WebhookSubscriptionId(UUID.randomUUID());
    }

    public static WebhookSubscriptionId fromString(String value) {
        return new WebhookSubscriptionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WebhookSubscriptionId{" + value + "}";
    }
}
