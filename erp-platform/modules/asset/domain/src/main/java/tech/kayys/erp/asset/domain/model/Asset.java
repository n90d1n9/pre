package tech.kayys.erp.asset.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.asset.domain.identifier.AssetCategoryId;
import tech.kayys.erp.asset.domain.identifier.AssetId;
import tech.kayys.erp.asset.domain.valueobject.AssetStatus;
import tech.kayys.erp.asset.domain.valueobject.AssetType;
import tech.kayys.erp.asset.domain.valueobject.Money;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Asset aggregate root.
 * Represents a fixed asset owned by the organization.
 */
public final class Asset extends AggregateRoot<AssetId> {
    
    private static final long serialVersionUID = 1L;
    
    private String assetNumber;
    private String serialNumber;
    private String name;
    private String description;
    private AssetType assetType;
    private AssetCategoryId categoryId;
    private String categoryName;
    private AssetStatus status;
    private Money purchasePrice;
    private Money currentValue;
    private Money accumulatedDepreciation;
    private Money salvageValue;
    private LocalDate purchaseDate;
    private LocalDate acquisitionDate;
    private LocalDate disposalDate;
    private String supplier;
    private String invoiceNumber;
    private String purchaseOrderNumber;
    private String location;
    private String department;
    private String assignedTo;
    private String responsiblePerson;
    private int usefulLifeYears;
    private double depreciationRate;
    private String depreciationMethod;
    private String warrantyEndDate;
    private String insurancePolicyNumber;
    private String insuranceCompany;
    private List<MaintenanceRecord> maintenanceRecords;
    private List<DepreciationEntry> depreciationEntries;
    private String notes;
    private boolean active;
    private String currencyCode;

    private Asset(AssetId id) {
        super(id);
        this.maintenanceRecords = new ArrayList<>();
        this.depreciationEntries = new ArrayList<>();
        this.status = AssetStatus.ACTIVE;
        this.active = true;
    }

    private Asset() {
        super();
    }

    /**
     * Factory method to create a new asset.
     */
    public static Asset create(
            AssetId id,
            String assetNumber,
            String name,
            AssetType assetType,
            Money purchasePrice,
            LocalDate purchaseDate,
            String currencyCode) {
        Asset asset = new Asset(id);
        asset.assetNumber = assetNumber;
        asset.name = name;
        asset.assetType = assetType;
        asset.purchasePrice = purchasePrice;
        asset.currentValue = purchasePrice;
        asset.purchaseDate = purchaseDate;
        asset.acquisitionDate = purchaseDate;
        asset.currencyCode = currencyCode;
        asset.accumulatedDepreciation = Money.zero(currencyCode);
        asset.salvageValue = Money.zero(currencyCode);
        return asset;
    }

    /**
     * Sets the category for the asset.
     */
    public void setCategory(AssetCategoryId categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets depreciation information.
     */
    public void setDepreciationInfo(
            int usefulLifeYears,
            String depreciationMethod,
            double depreciationRate,
            Money salvageValue) {
        this.usefulLifeYears = usefulLifeYears;
        this.depreciationMethod = depreciationMethod;
        this.depreciationRate = depreciationRate;
        this.salvageValue = salvageValue;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records depreciation for a period.
     */
    public void recordDepreciation(DepreciationEntry entry) {
        if (status == AssetStatus.DISPOSED || status == AssetStatus.LOST || status == AssetStatus.STOLEN) {
            throw new IllegalStateException("Cannot depreciate disposed asset");
        }
        depreciationEntries.add(entry);
        this.accumulatedDepreciation = accumulatedDepreciation.add(entry.getAmount());
        this.currentValue = purchasePrice.subtract(accumulatedDepreciation);
        if (currentValue.isLessThanOrEqualTo(Money.zero(currencyCode))) {
            this.currentValue = Money.zero(currencyCode);
            this.status = AssetStatus.DEPRECIATED;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the asset location.
     */
    public void updateLocation(String location, String department) {
        this.location = location;
        this.department = department;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Assigns the asset to someone.
     */
    public void assign(String assignedTo, String responsiblePerson) {
        this.assignedTo = assignedTo;
        this.responsiblePerson = responsiblePerson;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records maintenance.
     */
    public void recordMaintenance(MaintenanceRecord record) {
        maintenanceRecords.add(record);
        if (record.getStatus() == MaintenanceRecord.MaintenanceStatus.IN_PROGRESS) {
            this.status = AssetStatus.MAINTENANCE;
        } else if (record.getStatus() == MaintenanceRecord.MaintenanceStatus.COMPLETED) {
            this.status = AssetStatus.ACTIVE;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Disposes the asset.
     */
    public void dispose(LocalDate disposalDate, String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Asset already disposed");
        }
        this.status = AssetStatus.DISPOSED;
        this.disposalDate = disposalDate;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the asset as lost.
     */
    public void markLost(String reason) {
        this.status = AssetStatus.LOST;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks the asset as stolen.
     */
    public void markStolen(String reason) {
        this.status = AssetStatus.STOLEN;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the depreciation percentage.
     */
    public double getDepreciationPercentage() {
        if (purchasePrice.isZero()) {
            return 0.0;
        }
        return accumulatedDepreciation.getAmount()
            .divide(purchasePrice.getAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(java.math.BigDecimal.valueOf(100))
            .doubleValue();
    }

    /**
     * Gets the asset age in years.
     */
    public double getAgeInYears() {
        if (acquisitionDate == null) {
            return 0.0;
        }
        return (double) java.time.temporal.ChronoUnit.DAYS.between(acquisitionDate, LocalDate.now()) / 365.25;
    }

    /**
     * Checks if the asset is fully depreciated.
     */
    public boolean isFullyDepreciated() {
        return currentValue.isZero() || currentValue.isLessThanOrEqualTo(salvageValue);
    }

    // Getters
    public String getAssetNumber() { return assetNumber; }
    public String getSerialNumber() { return serialNumber; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public AssetType getAssetType() { return assetType; }
    public AssetCategoryId getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public AssetStatus getStatus() { return status; }
    public Money getPurchasePrice() { return purchasePrice; }
    public Money getCurrentValue() { return currentValue; }
    public Money getAccumulatedDepreciation() { return accumulatedDepreciation; }
    public Money getSalvageValue() { return salvageValue; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public LocalDate getAcquisitionDate() { return acquisitionDate; }
    public LocalDate getDisposalDate() { return disposalDate; }
    public String getSupplier() { return supplier; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
    public String getLocation() { return location; }
    public String getDepartment() { return department; }
    public String getAssignedTo() { return assignedTo; }
    public String getResponsiblePerson() { return responsiblePerson; }
    public int getUsefulLifeYears() { return usefulLifeYears; }
    public double getDepreciationRate() { return depreciationRate; }
    public String getDepreciationMethod() { return depreciationMethod; }
    public String getWarrantyEndDate() { return warrantyEndDate; }
    public String getInsurancePolicyNumber() { return insurancePolicyNumber; }
    public String getInsuranceCompany() { return insuranceCompany; }
    public List<MaintenanceRecord> getMaintenanceRecords() { return Collections.unmodifiableList(maintenanceRecords); }
    public List<DepreciationEntry> getDepreciationEntries() { return Collections.unmodifiableList(depreciationEntries); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public String getCurrencyCode() { return currencyCode; }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setWarrantyEndDate(String warrantyEndDate) {
        this.warrantyEndDate = warrantyEndDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInsurancePolicyNumber(String insurancePolicyNumber) {
        this.insurancePolicyNumber = insurancePolicyNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
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
        return "Asset{" +
                "id=" + getId() +
                ", assetNumber='" + assetNumber + '\'' +
                ", name='" + name + '\'' +
                ", type=" + assetType +
                ", status=" + status +
                ", purchasePrice=" + purchasePrice +
                ", currentValue=" + currentValue +
                '}';
    }

    /**
     * Maintenance record value object.
     */
    public static final class MaintenanceRecord implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final LocalDate scheduledDate;
        private LocalDate completedDate;
        private final String type;
        private final String description;
        private final String performedBy;
        private final String cost;
        private final MaintenanceStatus status;
        private final String notes;

        public MaintenanceRecord(
                String id,
                LocalDate scheduledDate,
                String type,
                String description,
                String performedBy,
                String cost) {
            this.id = id;
            this.scheduledDate = scheduledDate;
            this.type = type;
            this.description = description;
            this.performedBy = performedBy;
            this.cost = cost;
            this.status = MaintenanceStatus.SCHEDULED;
            this.completedDate = null;
            this.notes = null;
            validate();
        }

        public MaintenanceRecord(
                String id,
                LocalDate scheduledDate,
                String type,
                String description,
                String performedBy,
                String cost,
                MaintenanceStatus status,
                LocalDate completedDate,
                String notes) {
            this.id = id;
            this.scheduledDate = scheduledDate;
            this.type = type;
            this.description = description;
            this.performedBy = performedBy;
            this.cost = cost;
            this.status = status;
            this.completedDate = completedDate;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Maintenance ID cannot be empty");
            }
            if (scheduledDate == null) {
                throw new IllegalArgumentException("Scheduled date cannot be null");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Maintenance type cannot be empty");
            }
        }

        public String getId() { return id; }
        public LocalDate getScheduledDate() { return scheduledDate; }
        public LocalDate getCompletedDate() { return completedDate; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public String getPerformedBy() { return performedBy; }
        public String getCost() { return cost; }
        public MaintenanceStatus getStatus() { return status; }
        public String getNotes() { return notes; }

        public MaintenanceRecord complete(LocalDate completedDate, String notes) {
            return new MaintenanceRecord(
                id, scheduledDate, type, description, performedBy, cost,
                MaintenanceStatus.COMPLETED, completedDate, notes
            );
        }

        public enum MaintenanceStatus {
            SCHEDULED("Scheduled"),
            IN_PROGRESS("In Progress"),
            COMPLETED("Completed"),
            CANCELLED("Cancelled");

            private final String displayName;

            MaintenanceStatus(String displayName) {
                this.displayName = displayName;
            }

            public String getDisplayName() {
                return displayName;
            }
        }

        @Override
        public String toString() {
            return "MaintenanceRecord{" +
                    "id='" + id + '\'' +
                    ", scheduledDate=" + scheduledDate +
                    ", type='" + type + '\'' +
                    ", status=" + status +
                    '}';
        }
    }

    /**
     * Depreciation entry value object.
     */
    public static final class DepreciationEntry implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final LocalDate periodDate;
        private final Money amount;
        private final Money accumulatedDepreciation;
        private final Money bookValue;
        private final String period;
        private final String notes;

        public DepreciationEntry(
                String id,
                LocalDate periodDate,
                Money amount,
                Money accumulatedDepreciation,
                Money bookValue,
                String period,
                String notes) {
            this.id = id;
            this.periodDate = periodDate;
            this.amount = amount;
            this.accumulatedDepreciation = accumulatedDepreciation;
            this.bookValue = bookValue;
            this.period = period;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Depreciation entry ID cannot be empty");
            }
            if (periodDate == null) {
                throw new IllegalArgumentException("Period date cannot be null");
            }
            if (amount == null || amount.isNegative()) {
                throw new IllegalArgumentException("Amount must be positive");
            }
        }

        public String getId() { return id; }
        public LocalDate getPeriodDate() { return periodDate; }
        public Money getAmount() { return amount; }
        public Money getAccumulatedDepreciation() { return accumulatedDepreciation; }
        public Money getBookValue() { return bookValue; }
        public String getPeriod() { return period; }
        public String getNotes() { return notes; }

        @Override
        public String toString() {
            return "DepreciationEntry{" +
                    "id='" + id + '\'' +
                    ", periodDate=" + periodDate +
                    ", amount=" + amount +
                    ", bookValue=" + bookValue +
                    '}';
        }
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>
</modules>