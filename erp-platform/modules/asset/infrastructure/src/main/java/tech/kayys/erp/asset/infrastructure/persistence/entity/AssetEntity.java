package tech.kayys.erp.asset.infrastructure.persistence.entity;

import tech.kayys.erp.foundation.persistence.BaseEntity;
import tech.kayys.erp.asset.domain.valueobject.AssetStatus;
import tech.kayys.erp.asset.domain.valueobject.AssetType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Asset entity for persistence.
 */
@Entity
@Table(name = "assets", indexes = {
    @Index(name = "idx_asset_number", columnList = "asset_number"),
    @Index(name = "idx_asset_type", columnList = "asset_type"),
    @Index(name = "idx_asset_status", columnList = "status"),
    @Index(name = "idx_asset_category", columnList = "category_id"),
    @Index(name = "idx_asset_assigned", columnList = "assigned_to")
})
public class AssetEntity extends BaseEntity {

    @Column(name = "asset_number", unique = true, nullable = false, length = 50)
    public String assetNumber;

    @Column(name = "serial_number", length = 50)
    public String serialNumber;

    @Column(name = "name", nullable = false, length = 255)
    public String name;

    @Column(name = "description", length = 2000)
    public String description;

    @Column(name = "asset_type", nullable = false)
    @Enumerated(EnumType.STRING)
    public AssetType assetType;

    @Column(name = "category_id", columnDefinition = "UUID")
    public UUID categoryId;

    @Column(name = "category_name", length = 100)
    public String categoryName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    public AssetStatus status;

    @Column(name = "purchase_price", precision = 19, scale = 2)
    public BigDecimal purchasePrice;

    @Column(name = "current_value", precision = 19, scale = 2)
    public BigDecimal currentValue;

    @Column(name = "accumulated_depreciation", precision = 19, scale = 2)
    public BigDecimal accumulatedDepreciation;

    @Column(name = "salvage_value", precision = 19, scale = 2)
    public BigDecimal salvageValue;

    @Column(name = "purchase_date")
    public LocalDate purchaseDate;

    @Column(name = "acquisition_date")
    public LocalDate acquisitionDate;

    @Column(name = "disposal_date")
    public LocalDate disposalDate;

    @Column(name = "supplier", length = 100)
    public String supplier;

    @Column(name = "invoice_number", length = 50)
    public String invoiceNumber;

    @Column(name = "purchase_order_number", length = 50)
    public String purchaseOrderNumber;

    @Column(name = "location", length = 100)
    public String location;

    @Column(name = "department", length = 100)
    public String department;

    @Column(name = "assigned_to", columnDefinition = "UUID")
    public UUID assignedTo;

    @Column(name = "responsible_person", length = 100)
    public String responsiblePerson;

    @Column(name = "useful_life_years")
    public int usefulLifeYears;

    @Column(name = "depreciation_rate")
    public double depreciationRate;

    @Column(name = "depreciation_method", length = 50)
    public String depreciationMethod;

    @Column(name = "warranty_end_date")
    public String warrantyEndDate;

    @Column(name = "insurance_policy_number", length = 50)
    public String insurancePolicyNumber;

    @Column(name = "insurance_company", length = 100)
    public String insuranceCompany;

    @Column(name = "currency_code", length = 3)
    public String currencyCode;

    @Column(name = "notes", length = 2000)
    public String notes;

    @ElementCollection
    @CollectionTable(name = "asset_maintenance_records", joinColumns = @JoinColumn(name = "asset_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "maintenance_id")),
        @AttributeOverride(name = "scheduledDate", column = @Column(name = "scheduled_date")),
        @AttributeOverride(name = "completedDate", column = @Column(name = "completed_date")),
        @AttributeOverride(name = "type", column = @Column(name = "maintenance_type", length = 50)),
        @AttributeOverride(name = "description", column = @Column(name = "description", length = 500)),
        @AttributeOverride(name = "performedBy", column = @Column(name = "performed_by", length = 100)),
        @AttributeOverride(name = "cost", column = @Column(name = "cost", precision = 19, scale = 2)),
        @AttributeOverride(name = "status", column = @Column(name = "status", length = 20)),
        @AttributeOverride(name = "notes", column = @Column(name = "notes", length = 500))
    })
    public List<MaintenanceRecordEntity> maintenanceRecords = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "asset_depreciation_entries", joinColumns = @JoinColumn(name = "asset_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "entry_id")),
        @AttributeOverride(name = "periodDate", column = @Column(name = "period_date")),
        @AttributeOverride(name = "amount", column = @Column(name = "amount", precision = 19, scale = 2)),
        @AttributeOverride(name = "accumulatedDepreciation", column = @Column(name = "accumulated", precision = 19, scale = 2)),
        @AttributeOverride(name = "bookValue", column = @Column(name = "book_value", precision = 19, scale = 2)),
        @AttributeOverride(name = "period", column = @Column(name = "period", length = 20)),
        @AttributeOverride(name = "notes", column = @Column(name = "notes", length = 500))
    })
    public List<DepreciationEntryEntity> depreciationEntries = new ArrayList<>();

    @Embeddable
    public static class MaintenanceRecordEntity {
        public String id;
        public LocalDate scheduledDate;
        public LocalDate completedDate;
        public String type;
        public String description;
        public String performedBy;
        public BigDecimal cost;
        public String status;
        public String notes;
    }

    @Embeddable
    public static class DepreciationEntryEntity {
        public String id;
        public LocalDate periodDate;
        public BigDecimal amount;
        public BigDecimal accumulatedDepreciation;
        public BigDecimal bookValue;
        public String period;
        public String notes;
    }
}