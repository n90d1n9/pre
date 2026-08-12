package tech.kayys.erp.asset.application.api;

import tech.kayys.erp.asset.application.api.command.CreateAssetCommand;
import tech.kayys.erp.asset.application.api.command.DisposeAssetCommand;
import tech.kayys.erp.asset.application.api.query.AssetView;
import tech.kayys.erp.asset.domain.identifier.AssetId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for asset operations.
 */
public interface AssetService {

    /**
     * Creates a new asset.
     */
    CompletionStage<AssetId> createAsset(CreateAssetCommand command);

    /**
     * Disposes an asset.
     */
    CompletionStage<AssetId> disposeAsset(DisposeAssetCommand command);

    /**
     * Records depreciation for an asset.
     */
    CompletionStage<AssetId> recordDepreciation(AssetId assetId, String period);

    /**
     * Gets an asset by ID.
     */
    CompletionStage<AssetView> getAsset(AssetId assetId);

    /**
     * Searches assets with filters.
     */
    CompletionStage<List<AssetView>> searchAssets(
        String status, String type, String department, 
        String assignedTo, int page, int size
    );

    /**
     * Gets assets by category.
     */
    CompletionStage<List<AssetView>> getAssetsByCategory(String categoryId);

    /**
     * Gets assets by status.
     */
    CompletionStage<List<AssetView>> getAssetsByStatus(String status);

    /**
     * Processes depreciation for all assets.
     */
    CompletionStage<Integer> processDepreciation(String period);
}