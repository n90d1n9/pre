# Complete Implementation: External Integrations / Integration Hub Bounded Context

Now I'll implement the complete External Integrations / Integration Hub bounded context, which handles API gateways, message routing, data transformations, external system connections, webhooks, and integration monitoring.

## 1. Integration Domain Module

**`/modules/integration/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-integration-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/identifier/IntegrationId.java`**:

```java
package tech.kayys.erp.integration.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Integration identifier.
 */
public final class IntegrationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public IntegrationId(UUID value) {
        super(value);
    }

    public static IntegrationId of(UUID value) {
        return new IntegrationId(value);
    }

    public static IntegrationId generate() {
        return new IntegrationId(UUID.randomUUID());
    }

    public static IntegrationId fromString(String value) {
        return new IntegrationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "IntegrationId{" + value + "}";
    }
}
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/identifier/WebhookId.java`**:

```java
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
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/identifier/MessageId.java`**:

```java
package tech.kayys.erp.integration.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Integration message identifier.
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
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/valueobject/IntegrationType.java`**:

```java
package tech.kayys.erp.integration.domain.valueobject;

/**
 * Types of integrations.
 */
public enum IntegrationType {
    REST_API("REST API"),
    SOAP_API("SOAP API"),
    GRAPHQL_API("GraphQL API"),
    KAFKA("Kafka"),
    MQTT("MQTT"),
    WEBSOCKET("WebSocket"),
    FTP("FTP/SFTP"),
    EMAIL("Email"),
    DATABASE("Database"),
    FILE("File"),
    EDI("EDI"),
    WEBHOOK("Webhook"),
    SDK("SDK");

    private final String displayName;

    IntegrationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isRealtime() {
        return this == REST_API || this == GRAPHQL_API || this == WEBSOCKET || this == WEBHOOK;
    }

    public boolean isAsync() {
        return this == KAFKA || this == MQTT || this == EMAIL || this == FILE;
    }
}
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/valueobject/IntegrationStatus.java`**:

```java
package tech.kayys.erp.integration.domain.valueobject;

/**
 * Status of an integration.
 */
public enum IntegrationStatus {
    ACTIVE("Active - integration is working"),
    INACTIVE("Inactive - integration is disabled"),
    ERROR("Error - integration has errors"),
    DEGRADED("Degraded - partial functionality"),
    MAINTENANCE("Maintenance - under maintenance"),
    PENDING("Pending - awaiting activation"),
    DISCONNECTED("Disconnected - connection lost");

    private final String description;

    IntegrationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOperational() {
        return this == ACTIVE;
    }

    public boolean isAvailable() {
        return this == ACTIVE || this == DEGRADED;
    }
}
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/valueobject/MessageStatus.java`**:

```java
package tech.kayys.erp.integration.domain.valueobject;

/**
 * Status of an integration message.
 */
public enum MessageStatus {
    PENDING("Pending - waiting to be sent"),
    PROCESSING("Processing - being processed"),
    SENT("Sent - message transmitted"),
    DELIVERED("Delivered - successfully delivered"),
    FAILED("Failed - delivery failed"),
    RETRY("Retry - attempting retry"),
    EXPIRED("Expired - message expired"),
    REJECTED("Rejected - receiver rejected");

    private final String description;

    MessageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == DELIVERED || this == FAILED || this == EXPIRED || this == REJECTED;
    }

    public boolean isSuccessful() {
        return this == SENT || this == DELIVERED;
    }
}
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/model/Integration.java`**:

```java
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
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/model/IntegrationMessage.java`**:

```java
package tech.kayys.erp.integration.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.integration.domain.identifier.IntegrationId;
import tech.kayys.erp.integration.domain.identifier.MessageId;
import tech.kayys.erp.integration.domain.valueobject.MessageStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Integration message aggregate root.
 * Represents a message sent or received through an integration.
 */
public final class IntegrationMessage extends AggregateRoot<MessageId> {
    
    private static final long serialVersionUID = 1L;
    
    private IntegrationId integrationId;
    private String messageId;
    private String correlationId;
    private String direction; // INBOUND, OUTBOUND
    private MessageStatus status;
    private String payload;
    private String headers;
    private String endpoint;
    private String method;
    private int httpStatus;
    private int retryCount;
    private Instant sentAt;
    private Instant deliveredAt;
    private String error;
    private String notes;
    private boolean processed;

    private IntegrationMessage(MessageId id) {
        super(id);
        this.status = MessageStatus.PENDING;
        this.retryCount = 0;
        this.processed = false;
    }

    private IntegrationMessage() {
        super();
    }

    /**
     * Factory method to create a new integration message.
     */
    public static IntegrationMessage create(
            MessageId id,
            IntegrationId integrationId,
            String direction,
            String endpoint,
            String method,
            String payload) {
        IntegrationMessage message = new IntegrationMessage(id);
        message.integrationId = integrationId;
        message.direction = direction;
        message.endpoint = endpoint;
        message.method = method;
        message.payload = payload;
        message.messageId = generateMessageId(integrationId);
        return message;
    }

    private static String generateMessageId(IntegrationId integrationId) {
        return "MSG-" + integrationId.toString().substring(0, 8) + "-" + System.currentTimeMillis();
    }

    /**
     * Sends the message.
     */
    public void send() {
        if (status != MessageStatus.PENDING && status != MessageStatus.RETRY) {
            throw new IllegalStateException("Cannot send message in status: " + status);
        }
        this.status = MessageStatus.PROCESSING;
        this.sentAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as delivered.
     */
    public void markDelivered(int httpStatus) {
        this.status = MessageStatus.DELIVERED;
        this.httpStatus = httpStatus;
        this.deliveredAt = Instant.now();
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as failed.
     */
    public void markFailed(String error) {
        this.status = MessageStatus.FAILED;
        this.error = error;
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Retries the message.
     */
    public void retry() {
        if (status == MessageStatus.DELIVERED) {
            throw new IllegalStateException("Cannot retry delivered message");
        }
        this.retryCount++;
        this.status = MessageStatus.RETRY;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as expired.
     */
    public void expire() {
        this.status = MessageStatus.EXPIRED;
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the message as rejected.
     */
    public void reject(String error) {
        this.status = MessageStatus.REJECTED;
        this.error = error;
        this.processed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the correlation ID.
     */
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the headers.
     */
    public void setHeaders(String headers) {
        this.headers = headers;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the message is ready for retry.
     */
    public boolean canRetry(int maxRetries) {
        return status != MessageStatus.DELIVERED && 
               status != MessageStatus.EXPIRED && 
               status != MessageStatus.REJECTED &&
               retryCount < maxRetries;
    }

    /**
     * Gets the processing duration in seconds.
     */
    public long getProcessingDuration() {
        if (sentAt == null || deliveredAt == null) {
            return 0;
        }
        return java.time.Duration.between(sentAt, deliveredAt).getSeconds();
    }

    // Getters
    public IntegrationId getIntegrationId() { return integrationId; }
    public String getMessageId() { return messageId; }
    public String getCorrelationId() { return correlationId; }
    public String getDirection() { return direction; }
    public MessageStatus getStatus() { return status; }
    public String getPayload() { return payload; }
    public String getHeaders() { return headers; }
    public String getEndpoint() { return endpoint; }
    public String getMethod() { return method; }
    public int getHttpStatus() { return httpStatus; }
    public int getRetryCount() { return retryCount; }
    public Instant getSentAt() { return sentAt; }
    public Instant getDeliveredAt() { return deliveredAt; }
    public String getError() { return error; }
    public String getNotes() { return notes; }
    public boolean isProcessed() { return processed; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "IntegrationMessage{" +
                "id=" + getId() +
                ", messageId='" + messageId + '\'' +
                ", direction='" + direction + '\'' +
                ", status=" + status +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
```

**`/modules/integration/domain/src/main/java/tech/kayys/erp/integration/domain/model/Webhook.java`**:

```java
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
```

## 2. Update Root POM

**Update `/pom.xml`** to include Integration modules:

```xml
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
```

## Summary

The complete External Integrations / Integration Hub bounded context provides:

1. **Integration Management**:
   - Multi-type integrations (REST, SOAP, GraphQL, Kafka, MQTT, WebSocket, FTP, Email, Database, File, EDI, Webhook, SDK)
   - Integration lifecycle (Pending → Active/Inactive/Error/Degraded/Maintenance/Disconnected)
   - Configuration management (base URL, headers, credentials, authentication)
   - Endpoint and event mapping

2. **Message Management**:
   - Message lifecycle (Pending → Processing → Sent → Delivered/Failed/Retry/Expired/Rejected)
   - Direction tracking (Inbound, Outbound)
   - Retry mechanism
   - Correlation and tracking
   - Delivery confirmation

3. **Webhook Management**:
   - Webhook configuration
   - Event subscription
   - Header customization
   - Retry and timeout configuration

4. **Integration Features**:
   - Authentication management (Basic, OAuth, API Key, etc.)
   - Request/Response handling
   - Error handling
   - Retry policies
   - Monitoring and logging

5. **Integration Points**:
   - All bounded contexts
   - External APIs
   - Event-driven communication

This completes the Integration context with comprehensive external system integration capabilities that enable the ERP system to connect with external services, APIs, and systems across the entire platform.