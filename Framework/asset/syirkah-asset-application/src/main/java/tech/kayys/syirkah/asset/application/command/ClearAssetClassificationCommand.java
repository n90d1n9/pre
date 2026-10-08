package tech.kayys.syirkah.asset.application.command;

import tech.kayys.syirkah.asset.domain.identifier.AssetId;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.Objects;

public record ClearAssetClassificationCommand(String tenantId, AssetId assetId) implements Command {
    public ClearAssetClassificationCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(assetId, "assetId cannot be null");
    }
}
