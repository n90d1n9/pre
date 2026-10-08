package tech.kayys.syirkah.asset.application.query;

import tech.kayys.syirkah.asset.domain.identifier.AssetId;

import java.util.Objects;

/**
 * Query for the nested component hierarchy of an asset (ASSET-17 §17.13).
 *
 * @param depth requested depth; {@code depth=1} returns direct children only.
 */
public record GetAssetHierarchyQuery(String tenantId, AssetId assetId, int depth) {

    public GetAssetHierarchyQuery {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(assetId, "assetId cannot be null");
    }
}
