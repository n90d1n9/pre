package tech.kayys.erp.asset.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.asset.domain.identifier.AssetCategoryId;
import tech.kayys.erp.asset.domain.valueobject.AssetType;
import tech.kayys.erp.asset.domain.valueobject.DepreciationMethod;

import java.time.Instant;

/**
 * Asset category aggregate root.
 * Defines asset classification and depreciation rules.
 */
public final class AssetCategory extends AggregateRoot<AssetCategoryId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private AssetType assetType;
    private DepreciationMethod depreciationMethod;
    private int usefulLifeYears;
    private double salvageValuePercentage;
    private String accountId; // GL Account
    private String depreciationAccountId;
    private String accumulatedDepreciationAccountId;
    private boolean active;
    private String notes;

    private AssetCategory(AssetCategoryId id) {
        super(id);
        this.active = true;
    }

    private AssetCategory() {
        super();
    }

    /**
     * Factory method to create a new asset category.
     */
    public static AssetCategory create(
            AssetCategoryId id,
            String code,
            String name,
            AssetType assetType,
            DepreciationMethod depreciationMethod,
            int usefulLifeYears) {
        AssetCategory category = new AssetCategory(id);
        category.code = code;
        category.name = name;
        category.assetType = assetType;
        category.depreciationMethod = depreciationMethod;
        category.usefulLifeYears = usefulLifeYears;
        return category;
    }

    /**
     * Updates the category information.
     */
    public void update(String name, String description, DepreciationMethod depreciationMethod, int usefulLifeYears) {
        this.name = name;
        this.description = description;
        this.depreciationMethod = depreciationMethod;
        this.usefulLifeYears = usefulLifeYears;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets accounting details.
     */
    public void setAccountingDetails(
            String accountId,
            String depreciationAccountId,
            String accumulatedDepreciationAccountId) {
        this.accountId = accountId;
        this.depreciationAccountId = depreciationAccountId;
        this.accumulatedDepreciationAccountId = accumulatedDepreciationAccountId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the category.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the category.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Calculates annual depreciation for an asset.
     */
    public double calculateAnnualDepreciation(double cost, double salvageValue) {
        return switch (depreciationMethod) {
            case STRAIGHT_LINE -> (cost - salvageValue) / usefulLifeYears;
            case DECLINING_BALANCE -> (cost - salvageValue) * 0.2;
            case DOUBLE_DECLINING -> (cost - salvageValue) * 0.4;
            case SUM_OF_YEARS_DIGITS -> {
                int sumYears = (usefulLifeYears * (usefulLifeYears + 1)) / 2;
                yield (cost - salvageValue) * usefulLifeYears / sumYears;
            }
            case UNITS_OF_PRODUCTION -> 0.0; // Needs usage data
            case MACRS -> 0.0; // Needs MACRS table
        };
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public AssetType getAssetType() { return assetType; }
    public DepreciationMethod getDepreciationMethod() { return depreciationMethod; }
    public int getUsefulLifeYears() { return usefulLifeYears; }
    public double getSalvageValuePercentage() { return salvageValuePercentage; }
    public String getAccountId() { return accountId; }
    public String getDepreciationAccountId() { return depreciationAccountId; }
    public String getAccumulatedDepreciationAccountId() { return accumulatedDepreciationAccountId; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setSalvageValuePercentage(double salvageValuePercentage) {
        this.salvageValuePercentage = salvageValuePercentage;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "AssetCategory{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", assetType=" + assetType +
                ", depreciationMethod=" + depreciationMethod +
                '}';
    }
}