package tech.kayys.erp.asset.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.asset.domain.identifier.AssetId;
import tech.kayys.erp.asset.domain.identifier.AssetCategoryId;
import tech.kayys.erp.asset.domain.model.Asset;
import tech.kayys.erp.asset.domain.valueobject.AssetStatus;
import tech.kayys.erp.asset.domain.valueobject.AssetType;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for Asset aggregates.
 */
public interface AssetRepository extends Repository<Asset, AssetId> {

    /**
     * Finds assets by status.
     */
    CompletionStage<List<Asset>> findByStatus(AssetStatus status);

    /**
     * Finds assets by type.
     */
    CompletionStage<List<Asset>> findByType(AssetType type);

    /**
     * Finds assets by category.
     */
    CompletionStage<List<Asset>> findByCategory(AssetCategoryId categoryId);

    /**
     * Finds assets assigned to a person.
     */
    CompletionStage<List<Asset>> findByAssignedTo(String assignedTo);

    /**
     * Finds assets by department.
     */
    CompletionStage<List<Asset>> findByDepartment(String department);

    /**
     * Finds assets by location.
     */
    CompletionStage<List<Asset>> findByLocation(String location);

    /**
     * Finds assets acquired between dates.
     */
    CompletionStage<List<Asset>> findAcquiredBetween(LocalDate start, LocalDate end);

    /**
     * Finds assets needing maintenance.
     */
    CompletionStage<List<Asset>> findAssetsNeedingMaintenance();

    /**
     * Finds assets fully depreciated.
     */
    CompletionStage<List<Asset>> findFullyDepreciatedAssets();

    /**
     * Finds assets by serial number.
     */
    CompletionStage<Asset> findBySerialNumber(String serialNumber);

    /**
     * Finds assets by asset number.
     */
    CompletionStage<Asset> findByAssetNumber(String assetNumber);

    /**
     * Counts assets by status.
     */
    CompletionStage<Long> countByStatus(AssetStatus status);

    /**
     * Counts assets by type.
     */
    CompletionStage<Long> countByType(AssetType type);
}