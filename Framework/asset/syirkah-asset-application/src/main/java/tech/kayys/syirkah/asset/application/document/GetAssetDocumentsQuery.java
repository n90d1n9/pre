package tech.kayys.syirkah.asset.application.document;

import tech.kayys.syirkah.asset.domain.identifier.AssetId;

import java.util.Objects;

/** List all document references of one asset (tenant-scoped). */
public record GetAssetDocumentsQuery(String tenantId, AssetId assetId) {

    public GetAssetDocumentsQuery {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(assetId, "assetId cannot be null");
    }
}
