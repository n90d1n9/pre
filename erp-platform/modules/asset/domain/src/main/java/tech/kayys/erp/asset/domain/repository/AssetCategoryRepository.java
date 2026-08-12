package tech.kayys.erp.asset.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.asset.domain.identifier.AssetCategoryId;
import tech.kayys.erp.asset.domain.model.AssetCategory;
import tech.kayys.erp.asset.domain.valueobject.AssetType;
import tech.kayys.erp.asset.domain.valueobject.DepreciationMethod;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for AssetCategory aggregates.
 */
public interface AssetCategoryRepository extends Repository<AssetCategory, AssetCategoryId> {

    /**
     * Finds categories by asset type.
     */
    CompletionStage<List<AssetCategory>> findByAssetType(AssetType assetType);

    /**
     * Finds categories by depreciation method.
     */
    CompletionStage<List<AssetCategory>> findByDepreciationMethod(DepreciationMethod method);

    /**
     * Finds active categories.
     */
    CompletionStage<List<AssetCategory>> findActiveCategories();

    /**
     * Finds category by code.
     */
    CompletionStage<AssetCategory> findByCode(String code);

    /**
     * Finds categories by name containing text.
     */
    CompletionStage<List<AssetCategory>> findByNameContaining(String name);

    /**
     * Checks if code is unique.
     */
    CompletionStage<Boolean> isCodeUnique(String code);
}