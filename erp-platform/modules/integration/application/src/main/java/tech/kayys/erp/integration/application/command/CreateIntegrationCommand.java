package tech.kayys.erp.integration.application.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.integration.domain.valueobject.IntegrationType;

/**
 * Command to create a new integration.
 */
public class CreateIntegrationCommand implements Command {
    
    private String code;
    private String name;
    private IntegrationType type;
    private String baseUrl;
    private String authType;
    private String description;

    public CreateIntegrationCommand() {}

    public CreateIntegrationCommand(String code, String name, IntegrationType type, String baseUrl, String authType) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.baseUrl = baseUrl;
        this.authType = authType;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public IntegrationType getType() { return type; }
    public void setType(IntegrationType type) { this.type = type; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
