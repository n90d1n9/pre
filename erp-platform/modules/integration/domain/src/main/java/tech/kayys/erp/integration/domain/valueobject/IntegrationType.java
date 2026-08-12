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