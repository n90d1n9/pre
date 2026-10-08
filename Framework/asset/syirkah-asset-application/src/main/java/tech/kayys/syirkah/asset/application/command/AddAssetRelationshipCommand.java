package tech.kayys.syirkah.asset.application.command;

import tech.kayys.syirkah.asset.domain.identifier.AssetId;
import tech.kayys.syirkah.asset.domain.relationship.AssetRelationshipType;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.Objects;

public record AddAssetRelationshipCommand(String tenantId, AssetId sourceAssetId, AssetId relatedAssetId,
                                          AssetRelationshipType relationshipType) implements Command {
    public AddAssetRelationshipCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(sourceAssetId, "sourceAssetId cannot be null");
        Objects.requireNonNull(relatedAssetId, "relatedAssetId cannot be null");
        Objects.requireNonNull(relationshipType, "relationshipType cannot be null");
    }
}
