package tech.kayys.erp.integration.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.integration.domain.identifier.WebhookId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Webhook aggregate root.
 * Represents a webhook configuration for external callbacks.
 */
public final class Webhook extends AggregateRoot<WebhookId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String url;
    private String method;
    private String authType;
    private String authToken;
    private List<String> events;
    private List<String> headers;
    private int retryCount;
    private int timeoutSeconds;
    private boolean active;
    private boolean enabled;
    private String notes;

    private Webhook(WebhookId id) {
        super(id);
        this.events = new ArrayList<>();
        this.headers = new ArrayList<>();
        this.active = true;
        this.enabled = true;
        this.method = "POST";
        this.retryCount = 3;
        this.timeoutSeconds = 10;
    }

    private Webhook() {
        super();
    }

    /**
     * Factory method to create a new webhook.
     */
    public static Webhook create(
            WebhookId id,
            String code,
            String name,
            String url,
            String method,
            String authType) {
        Webhook webhook = new Webhook(id);
        webhook.code = code;
        webhook.name = name;
        webhook.url = url;
        webhook.method = method;
        webhook.authType = authType;
        return webhook;
    }

    /**
     * Adds an event to the webhook.
     */
    public void addEvent(String event) {
        if (!events.contains(event)) {
            events.add(event);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes an event from the webhook.
     */
    public void removeEvent(String event) {
        events.remove(event);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a header to the webhook.
     */
    public void addHeader(String header) {
        if (!headers.contains(header)) {
            headers.add(header);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a header from the webhook.
     */
    public void removeHeader(String header) {
        headers.remove(header);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Enables the webhook.
     */
    public void enable() {
        this.enabled = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Disables the webhook.
     */
    public void disable() {
        this.enabled = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the webhook.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the webhook.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the webhook is ready.
     */
    public boolean isReady() {
        return active && enabled;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public String getMethod() { return method; }
    public String getAuthType() { return authType; }
    public String getAuthToken() { return authToken; }
    public List<String> getEvents() { return Collections.unmodifiableList(events); }
    public List<String> getHeaders() { return Collections.unmodifiableList(headers); }
    public int getRetryCount() { return retryCount; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public boolean isActive() { return active; }
    public boolean isEnabled() { return enabled; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Webhook{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", events=" + events.size() +
                ", enabled=" + enabled +
                '}';
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>

    <module>modules/workforce/domain</module>
    <module>modules/workforce/application</module>
    <module>modules/workforce/infrastructure</module>
    <module>modules/workforce/interfaces</module>

    <module>modules/risk/domain</module>
    <module>modules/risk/application</module>
    <module>modules/risk/infrastructure</module>
    <module>modules/risk/interfaces</module>

    <module>modules/workflow/domain</module>
    <module>modules/workflow/application</module>
    <module>modules/workflow/infrastructure</module>
    <module>modules/workflow/interfaces</module>

    <module>modules/integration/domain</module>
    <module>modules/integration/application</module>
    <module>modules/integration/infrastructure</module>
    <module>modules/integration/interfaces</module>
</modules>