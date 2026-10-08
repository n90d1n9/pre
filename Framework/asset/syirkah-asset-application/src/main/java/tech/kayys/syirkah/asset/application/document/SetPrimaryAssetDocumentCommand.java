package tech.kayys.syirkah.asset.application.document;

import tech.kayys.syirkah.asset.domain.document.AssetDocumentReferenceId;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.Objects;

/** Designate one reference as the primary document of its type (ASSET-24 §19/§21). */
public record SetPrimaryAssetDocumentCommand(
        String tenantId,
        AssetDocumentReferenceId referenceId
) implements Command {

    public SetPrimaryAssetDocumentCommand {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(referenceId, "referenceId cannot be null");
    }
}
