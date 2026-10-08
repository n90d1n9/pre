package tech.kayys.syirkah.asset.application.command;

import tech.kayys.syirkah.asset.domain.classification.AssetAttribute;
import tech.kayys.syirkah.asset.domain.identifier.AssetId;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.List;
import java.util.Objects;

/**
 * Replaces an asset's dynamic attributes (ASSET-15 §15.13).
 *
 * <p>Attributes live outside the Asset aggregate, so this command writes to the
 * metadata repository rather than mutating the aggregate.</p>
 */
public record ReplaceAssetMetadataCommand(
        String tenantId,
        AssetId assetId,
        List<AssetAttribute> attributes
) implements Command {

    public ReplaceAssetMetadataCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(assetId, "assetId cannot be null");
        Objects.requireNonNull(attributes, "attributes cannot be null");
        attributes = List.copyOf(attributes);
        for (AssetAttribute attribute : attributes) {
            Objects.requireNonNull(attribute, "attribute cannot be null");
        }
    }
}
