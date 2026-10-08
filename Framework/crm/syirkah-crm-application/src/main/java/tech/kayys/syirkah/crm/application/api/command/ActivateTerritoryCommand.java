package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.crm.domain.territory.TerritoryId;
import tech.kayys.syirkah.foundation.application.command.Command;
import java.util.Objects;

/**
 * Command to activate a territory.
 */
public record ActivateTerritoryCommand(
        TerritoryId territoryId
) implements Command {

    public ActivateTerritoryCommand {
        Objects.requireNonNull(territoryId, "territoryId cannot be null");
    }
}