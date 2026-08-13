package tech.kayys.erp.integration.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.integration.domain.identifier.WebhookId;

import java.time.Instant;
import java.util.*;

/**
 * Webhook aggregate root.
 * Represents a webhook configuration for external integrations.
 */
public final class Webhook extends AggregateRoot<WebhookId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String url;
    private String secret;
    private List<String> events;
    private boolean active;
    private String description;
    private Map<String, String> headers;
    private int retryCount;
    private int timeoutSeconds;
    private Instant lastTriggeredAt;
    private int successCount;
    private int failureCount;

    private Webhook(WebhookId id) {
        super(id);
        this.events = new ArrayList<>();
        this.active = true;
        this.retryCount = 3;
        this.timeoutSeconds = 30;
        this.successCount = 0;
        this.failureCount = 0;
    }

    private Webhook() {
        super();
    }

    public static Webhook create(
            WebhookId id,
            String name,
            String url,
            String secret) {
        Webhook webhook = new Webhook(id);
        webhook.name = name;
        webhook.url = url;
        webhook.secret = secret;
        return webhook;
    }

    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
    }

    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
    }

    public void addEvent(String event) {
        if (!events.contains(event)) {
            events.add(event);
            setUpdatedAt(Instant.now());
        }
    }

    public void removeEvent(String event) {
        events.remove(event);
        setUpdatedAt(Instant.now());
    }

    public void trigger() {
        this.lastTriggeredAt = Instant.now();
        setUpdatedAt(Instant.now());
    }

    public void recordSuccess() {
        this.successCount++;
        setUpdatedAt(Instant.now());
    }

    public void recordFailure() {
        this.failureCount++;
        setUpdatedAt(Instant.now());
    }

    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getSecret() { return secret; }
    public List<String> getEvents() { return Collections.unmodifiableList(events); }
    public boolean isActive() { return active; }
    public String getDescription() { return description; }
    public Map<String, String> getHeaders() { return Collections.unmodifiableMap(headers != null ? headers : new HashMap<>()); }
    public int getRetryCount() { return retryCount; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public Instant getLastTriggeredAt() { return lastTriggeredAt; }
    public int getSuccessCount() { return successCount; }
    public int getFailureCount() { return failureCount; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
        setUpdatedAt(Instant.now());
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        setUpdatedAt(Instant.now());
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        setUpdatedAt(Instant.now());
    }

    @Override
    public String toString() {
        return "Webhook{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", active=" + active +
                '}';
    }
}
