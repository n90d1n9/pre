package tech.kayys.syirkah.asset.application.command;

import tech.kayys.syirkah.asset.domain.valueobject.AssetType;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.Objects;

public record CreateAssetCommand(
        String tenantId,
        String assetNumber,
        String name,
        AssetType type
) implements Command {

    public CreateAssetCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(assetNumber, "assetNumber cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
    }
}
