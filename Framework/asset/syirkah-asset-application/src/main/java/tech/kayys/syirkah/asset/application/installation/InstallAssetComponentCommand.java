package tech.kayys.syirkah.asset.application.installation;

import tech.kayys.syirkah.asset.domain.relationship.AssetRelationshipType;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.Objects;
import java.util.UUID;

/** Install a component asset on a parent asset (see ASSET-18). */
public record InstallAssetComponentCommand(
        String tenantId,
        UUID componentAssetId,
        UUID parentAssetId,
        AssetRelationshipType relationshipType,
        String installedBy
) implements Command {

    public InstallAssetComponentCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(componentAssetId, "componentAssetId cannot be null");
        Objects.requireNonNull(parentAssetId, "parentAssetId cannot be null");
        Objects.requireNonNull(relationshipType, "relationshipType cannot be null");
    }
}
