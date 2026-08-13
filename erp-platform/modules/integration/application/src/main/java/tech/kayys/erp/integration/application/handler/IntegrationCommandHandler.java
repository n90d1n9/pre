package tech.kayys.erp.integration.application.handler;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.integration.application.command.CreateIntegrationCommand;
import tech.kayys.erp.integration.domain.identifier.IntegrationId;
import tech.kayys.erp.integration.domain.model.Integration;

/**
 * Handler for integration commands.
 */
public class IntegrationCommandHandler implements CommandHandler<CreateIntegrationCommand, IntegrationId> {

    @Override
    public IntegrationId handle(CreateIntegrationCommand command) {
        IntegrationId id = IntegrationId.generate();
        Integration integration = Integration.create(
            id,
            command.getCode(),
            command.getName(),
            command.getType(),
            command.getBaseUrl(),
            command.getAuthType()
        );
        
        if (command.getDescription() != null) {
            integration.setDescription(command.getDescription());
        }
        
        integration.activate();
        return id;
    }
}
