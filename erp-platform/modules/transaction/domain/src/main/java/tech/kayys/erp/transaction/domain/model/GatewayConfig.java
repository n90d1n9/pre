package tech.kayys.erp.transaction.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Gateway configuration value object.
 * Stores credentials and configuration for payment gateways.
 */
public final class GatewayConfig implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String configId;
    private final GatewayProvider provider;
    private final String merchantId;
    private final String apiKey;
    private final String apiSecret;
    private final String publicKey;
    private final String webhookSecret;
    private final boolean isLiveMode;
    private final Map<String, String> additionalConfig;
    private final String endpointUrl;
    private final int timeoutSeconds;
    private final int retryAttempts;

    public GatewayConfig(
            String configId,
            GatewayProvider provider,
            String merchantId,
            String apiKey,
            String apiSecret,
            String publicKey,
            String webhookSecret,
            boolean isLiveMode,
            Map<String, String> additionalConfig,
            String endpointUrl,
            int timeoutSeconds,
            int retryAttempts) {
        this.configId = configId;
        this.provider = provider;
        this.merchantId = merchantId;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.publicKey = publicKey;
        this.webhookSecret = webhookSecret;
        this.isLiveMode = isLiveMode;
        this.additionalConfig = additionalConfig != null ? new HashMap<>(additionalConfig) : new HashMap<>();
        this.endpointUrl = endpointUrl;
        this.timeoutSeconds = timeoutSeconds;
        this.retryAttempts = retryAttempts;
        validate();
    }

    @Override
    public void validate() {
        if (configId == null || configId.trim().isEmpty()) {
            throw new IllegalArgumentException("Config ID cannot be empty");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("API key cannot be empty");
        }
    }

    // Getters
    public String getConfigId() { return configId; }
    public GatewayProvider getProvider() { return provider; }
    public String getMerchantId() { return merchantId; }
    public String getApiKey() { return apiKey; }
    public String getApiSecret() { return apiSecret; }
    public String getPublicKey() { return publicKey; }
    public String getWebhookSecret() { return webhookSecret; }
    public boolean isLiveMode() { return isLiveMode; }
    public Map<String, String> getAdditionalConfig() { return additionalConfig; }
    public String getEndpointUrl() { return endpointUrl; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public int getRetryAttempts() { return retryAttempts; }

    public String getMode() {
        return isLiveMode ? "LIVE" : "TEST";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GatewayConfig that = (GatewayConfig) o;
        return Objects.equals(configId, that.configId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configId);
    }

    @Override
    public String toString() {
        return "GatewayConfig{" +
                "configId='" + configId + '\'' +
                ", provider=" + provider +
                ", mode=" + getMode() +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String configId;
        private GatewayProvider provider;
        private String merchantId;
        private String apiKey;
        private String apiSecret;
        private String publicKey;
        private String webhookSecret;
        private boolean isLiveMode = false;
        private Map<String, String> additionalConfig = new HashMap<>();
        private String endpointUrl;
        private int timeoutSeconds = 30;
        private int retryAttempts = 3;

        public Builder configId(String configId) {
            this.configId = configId;
            return this;
        }

        public Builder provider(GatewayProvider provider) {
            this.provider = provider;
            return this;
        }

        public Builder merchantId(String merchantId) {
            this.merchantId = merchantId;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder publicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder webhookSecret(String webhookSecret) {
            this.webhookSecret = webhookSecret;
            return this;
        }

        public Builder isLiveMode(boolean isLiveMode) {
            this.isLiveMode = isLiveMode;
            return this;
        }

        public Builder additionalConfig(Map<String, String> additionalConfig) {
            this.additionalConfig = additionalConfig != null ? new HashMap<>(additionalConfig) : new HashMap<>();
            return this;
        }

        public Builder endpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
            return this;
        }

        public Builder timeoutSeconds(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder retryAttempts(int retryAttempts) {
            this.retryAttempts = retryAttempts;
            return this;
        }

        public GatewayConfig build() {
            if (configId == null) {
                configId = UUID.randomUUID().toString();
            }
            return new GatewayConfig(
                configId, provider, merchantId, apiKey, apiSecret,
                publicKey, webhookSecret, isLiveMode, additionalConfig,
                endpointUrl, timeoutSeconds, retryAttempts
            );
        }
    }
}