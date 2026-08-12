package tech.kayys.erp.integration.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.integration.domain.identifier.IntegrationId;
import tech.kayys.erp.integration.domain.valueobject.IntegrationStatus;
import tech.kayys.erp.integration.domain.valueobject.IntegrationType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Integration aggregate root.
 * Represents an external integration configuration.
 */
public final class Integration extends AggregateRoot<IntegrationId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private IntegrationType type;
    private IntegrationStatus status;
    private String baseUrl;
    private Map<String, String> headers;
    private Map<String, String> credentials;
    private Map<String, String> configuration;
    private String authType;
    private int timeoutSeconds;
    private int retryCount;
    private int retryDelaySeconds;
    private String version;
    private List<String> endpoints;
    private List<String> events;
    private String notes;
    private boolean active;

    private Integration(IntegrationId id) {
        super(id);
        this.endpoints = new ArrayList<>();
        this.events = new ArrayList<>();
        this.status = IntegrationStatus.PENDING;
        this.active = true;
        this.timeoutSeconds = 30;
        this.retryCount = 3;
        this.retryDelaySeconds = 5;
        this.version = "1.0";
    }

    private Integration() {
        super();
    }

    /**
     * Factory method to create a new integration.
     */
    public static Integration create(
            IntegrationId id,
            String code,
            String name,
            IntegrationType type,
            String baseUrl,
            String authType) {
        Integration integration = new Integration(id);
        integration.code = code;
        integration.name = name;
        integration.type = type;
        integration.baseUrl = baseUrl;
        integration.authType = authType;
        return integration;
    }

    /**
     * Activates the integration.
     */
    public void activate() {
        this.status = IntegrationStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the integration.
     */
    public void deactivate() {
        this.status = IntegrationStatus.INACTIVE;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the integration as error.
     */
    public void markError(String reason) {
        this.status = IntegrationStatus.ERROR;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the integration as degraded.
     */
    public void markDegraded(String reason) {
        this.status = IntegrationStatus.DEGRADED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the integration as under maintenance.
     */
    public void markMaintenance(String reason) {
        this.status = IntegrationStatus.MAINTENANCE;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an endpoint to the integration.
     */
    public void addEndpoint(String endpoint) {
        if (!endpoints.contains(endpoint)) {
            endpoints.add(endpoint);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes an endpoint from the integration.
     */
    public void removeEndpoint(String endpoint) {
        endpoints.remove(endpoint);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an event to the integration.
     */
    public void addEvent(String event) {
        if (!events.contains(event)) {
            events.add(event);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes an event from the integration.
     */
    public void removeEvent(String event) {
        events.remove(event);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the timeout configuration.
     */
    public void setTimeout(int timeoutSeconds, int retryCount, int retryDelaySeconds) {
        this.timeoutSeconds = timeoutSeconds;
        this.retryCount = retryCount;
        this.retryDelaySeconds = retryDelaySeconds;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the integration is ready.
     */
    public boolean isReady() {
        return status == IntegrationStatus.ACTIVE && active;
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public IntegrationType getType() { return type; }
    public IntegrationStatus getStatus() { return status; }
    public String getBaseUrl() { return baseUrl; }
    public Map<String, String> getHeaders() { return Collections.unmodifiableMap(headers); }
    public Map<String, String> getCredentials() { return Collections.unmodifiableMap(credentials); }
    public Map<String, String> getConfiguration() { return Collections.unmodifiableMap(configuration); }
    public String getAuthType() { return authType; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public int getRetryCount() { return retryCount; }
    public int getRetryDelaySeconds() { return retryDelaySeconds; }
    public String getVersion() { return version; }
    public List<String> getEndpoints() { return Collections.unmodifiableList(endpoints); }
    public List<String> getEvents() { return Collections.unmodifiableList(events); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCredentials(Map<String, String> credentials) {
        this.credentials = credentials;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void incrementVersion() {
        String[] parts = version.split("\\.");
        if (parts.length == 2) {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]) + 1;
            this.version = major + "." + minor;
        } else {
            this.version = "1.1";
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Integration{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}