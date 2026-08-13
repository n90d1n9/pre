package tech.kayys.erp.integration.application.query;

import tech.kayys.erp.foundation.application.Query;
import java.util.UUID;

/**
 * Query to get an integration by ID.
 */
public class GetIntegrationQuery implements Query {
    
    private UUID id;

    public GetIntegrationQuery() {}

    public GetIntegrationQuery(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
}
