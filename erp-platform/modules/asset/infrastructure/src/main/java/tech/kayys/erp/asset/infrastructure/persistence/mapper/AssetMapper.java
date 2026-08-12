package tech.kayys.erp.asset.infrastructure.persistence.mapper;

import tech.kayys.erp.asset.domain.identifier.AssetCategoryId;
import tech.kayys.erp.asset.domain.identifier.AssetId;
import tech.kayys.erp.asset.domain.model.Asset;
import tech.kayys.erp.asset.domain.valueobject.Money;
import tech.kayys.erp.asset.infrastructure.persistence.entity.AssetEntity;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Mapper between Asset domain and persistence entities.
 */
@ApplicationScoped
public class AssetMapper {

    public AssetEntity toEntity(Asset asset) {
        AssetEntity entity = new AssetEntity();
        entity.id = asset.getId().getValue();
        entity.assetNumber = asset.getAssetNumber();
        entity.serialNumber = asset.getSerialNumber();
        entity.name = asset.getName();
        entity.description = asset.getDescription();
        entity.assetType = asset.getAssetType();
        entity.categoryId = asset.getCategoryId() != null ? 
            asset.getCategoryId().getValue() : null;
        entity.categoryName = asset.getCategoryName();
        entity.status = asset.getStatus();
        entity.purchasePrice = asset.getPurchasePrice() != null ? 
            asset.getPurchasePrice().getAmount() : BigDecimal.ZERO;
        entity.currentValue = asset.getCurrentValue() != null ? 
            asset.getCurrentValue().getAmount() : BigDecimal.ZERO;
        entity.accumulatedDepreciation = asset.getAccumulatedDepreciation() != null ? 
            asset.getAccumulatedDepreciation().getAmount() : BigDecimal.ZERO;
        entity.salvageValue = asset.getSalvageValue() != null ? 
            asset.getSalvageValue().getAmount() : BigDecimal.ZERO;
        entity.purchaseDate = asset.getPurchaseDate();
        entity.acquisitionDate = asset.getAcquisitionDate();
        entity.disposalDate = asset.getDisposalDate();
        entity.supplier = asset.getSupplier();
        entity.invoiceNumber = asset.getInvoiceNumber();
        entity.purchaseOrderNumber = asset.getPurchaseOrderNumber();
        entity.location = asset.getLocation();
        entity.department = asset.getDepartment();
        entity.assignedTo = asset.getAssignedTo() != null ? 
            UUID.fromString(asset.getAssignedTo()) : null;
        entity.responsiblePerson = asset.getResponsiblePerson();
        entity.usefulLifeYears = asset.getUsefulLifeYears();
        entity.depreciationRate = asset.getDepreciationRate();
        entity.depreciationMethod = asset.getDepreciationMethod();
        entity.warrantyEndDate = asset.getWarrantyEndDate();
        entity.insurancePolicyNumber = asset.getInsurancePolicyNumber();
        entity.insuranceCompany = asset.getInsuranceCompany();
        entity.currencyCode = asset.getCurrencyCode();
        entity.notes = asset.getNotes();
        entity.active = asset.isActive();
        entity.version = asset.getVersion();
        entity.createdAt = asset.getCreatedAt();
        entity.updatedAt = asset.getUpdatedAt();
        return entity;
    }

    public Asset toDomain(AssetEntity entity) {
        Asset asset = new Asset(AssetId.of(entity.id));
        asset.setAssetNumber(entity.assetNumber);
        asset.setSerialNumber(entity.serialNumber);
        asset.setName(entity.name);
        asset.setDescription(entity.description);
        asset.setAssetType(entity.assetType);
        asset.setCategoryId(entity.categoryId != null ? 
            AssetCategoryId.of(entity.categoryId) : null);
        asset.setCategoryName(entity.categoryName);
        asset.setStatus(entity.status);
        
        if (entity.purchasePrice != null) {
            asset.setPurchasePrice(Money.of(entity.purchasePrice, entity.currencyCode != null ? 
                entity.currencyCode : "USD"));
        }
        if (entity.currentValue != null) {
            asset.setCurrentValue(Money.of(entity.currentValue, entity.currencyCode != null ? 
                entity.currencyCode : "USD"));
        }
        if (entity.accumulatedDepreciation != null) {
            asset.setAccumulatedDepreciation(Money.of(entity.accumulatedDepreciation, 
                entity.currencyCode != null ? entity.currencyCode : "USD"));
        }
        if (entity.salvageValue != null) {
            asset.setSalvageValue(Money.of(entity.salvageValue, entity.currencyCode != null ? 
                entity.currencyCode : "USD"));
        }
        
        asset.setPurchaseDate(entity.purchaseDate);
        asset.setAcquisitionDate(entity.acquisitionDate);
        asset.setDisposalDate(entity.disposalDate);
        asset.setSupplier(entity.supplier);
        asset.setInvoiceNumber(entity.invoiceNumber);
        asset.setPurchaseOrderNumber(entity.purchaseOrderNumber);
        asset.setLocation(entity.location);
        asset.setDepartment(entity.department);
        asset.setAssignedTo(entity.assignedTo != null ? 
            entity.assignedTo.toString() : null);
        asset.setResponsiblePerson(entity.responsiblePerson);
        asset.setUsefulLifeYears(entity.usefulLifeYears);
        asset.setDepreciationRate(entity.depreciationRate);
        asset.setDepreciationMethod(entity.depreciationMethod);
        asset.setWarrantyEndDate(entity.warrantyEndDate);
        asset.setInsurancePolicyNumber(entity.insurancePolicyNumber);
        asset.setInsuranceCompany(entity.insuranceCompany);
        asset.setCurrencyCode(entity.currencyCode);
        asset.setNotes(entity.notes);
        asset.setActive(entity.active);
        asset.setVersion(entity.version);
        asset.setCreatedAt(entity.createdAt);
        asset.setUpdatedAt(entity.updatedAt);
        
        // Maintenance records would be converted here
        // Depreciation entries would be converted here
        
        return asset;
    }
}