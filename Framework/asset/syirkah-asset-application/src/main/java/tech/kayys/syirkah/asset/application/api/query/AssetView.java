package tech.kayys.syirkah.asset.application.api.query;

import tech.kayys.syirkah.asset.domain.model.Asset;
import tech.kayys.syirkah.asset.domain.valueobject.AssetStatus;
import tech.kayys.syirkah.asset.domain.valueobject.AssetType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Complete view representation of an Asset.
 */
public record AssetView(
        String id,
        String assetNumber,
        String serialNumber,
        String name,
        String description,
        AssetType assetType,
        String categoryId,
        String categoryName,
        AssetStatus status,
        BigDecimal purchasePrice,
        BigDecimal currentValue,
        BigDecimal accumulatedDepreciation,
        BigDecimal salvageValue,
        LocalDate purchaseDate,
        LocalDate acquisitionDate,
        LocalDate disposalDate,
        String supplier,
        String invoiceNumber,
        String purchaseOrderNumber,
        String location,
        String department,
        String assignedTo,
        String responsiblePerson,
        Integer usefulLifeYears,
        Double depreciationRate,
        String depreciationMethod,
        String currencyCode,
        String notes,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
    public static AssetView fromDomain(Asset asset) {
        return new AssetView(
            asset.getId() != null ? asset.getId().toString() : null,
            asset.getAssetNumber(),
            asset.getSerialNumber(),
            asset.getName(),
            asset.getDescription(),
            asset.getAssetType(),
            asset.getCategoryId() != null ? asset.getCategoryId().toString() : null,
            asset.getCategoryName(),
            asset.getStatus(),
            asset.getPurchasePrice() != null ? asset.getPurchasePrice().getAmount() : null,
            asset.getCurrentValue() != null ? asset.getCurrentValue().getAmount() : null,
            asset.getAccumulatedDepreciation() != null ? asset.getAccumulatedDepreciation().getAmount() : null,
            asset.getSalvageValue() != null ? asset.getSalvageValue().getAmount() : null,
            asset.getPurchaseDate(),
            asset.getAcquisitionDate(),
            asset.getDisposalDate(),
            asset.getSupplier(),
            asset.getInvoiceNumber(),
            asset.getPurchaseOrderNumber(),
            asset.getLocation(),
            asset.getDepartment(),
            asset.getAssignedTo(),
            asset.getResponsiblePerson(),
            asset.getUsefulLifeYears(),
            asset.getDepreciationRate(),
            asset.getDepreciationMethod(),
            asset.getCurrencyCode(),
            asset.getNotes(),
            asset.isActive(),
            asset.getCreatedAt(),
            asset.getUpdatedAt()
        );
    }
}
