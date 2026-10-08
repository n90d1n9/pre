package tech.kayys.syirkah.asset.application.command;

import tech.kayys.syirkah.asset.domain.model.Asset;

import java.util.UUID;

public record CreateAssetResult(UUID assetId, String assetNumber, String status) {

    public static CreateAssetResult from(Asset asset) {
        return new CreateAssetResult(asset.id().value(), asset.assetNumber(), asset.status().name());
    }
}
