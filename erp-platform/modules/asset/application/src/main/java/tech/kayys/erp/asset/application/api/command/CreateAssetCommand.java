package tech.kayys.erp.asset.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.asset.domain.identifier.AssetId;
import tech.kayys.erp.asset.domain.valueobject.AssetType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to create a new asset.
 */
public record CreateAssetCommand(
        AssetId assetId,
        String assetNumber,
        String name,
        String description,
        AssetType assetType,
        UUID categoryId,
        String purchasePrice,
        String currencyCode,
        LocalDate purchaseDate,
        String supplier,
        String invoiceNumber,
        String purchaseOrderNumber,
        String location,
        String department,
        String assignedTo,
        String responsiblePerson,
        int usefulLifeYears,
        String depreciationMethod,
        String notes
) implements Command<AssetId> {

    public CreateAssetCommand {
        if (assetNumber == null || assetNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset number cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset name cannot be empty");
        }
        if (assetType == null) {
            throw new IllegalArgumentException("Asset type cannot be null");
        }
        if (purchasePrice == null || purchasePrice.trim().isEmpty()) {
            throw new IllegalArgumentException("Purchase price cannot be empty");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be empty");
        }
        if (purchaseDate == null) {
            throw new IllegalArgumentException("Purchase date cannot be null");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AssetId assetId;
        private String assetNumber;
        private String name;
        private String description;
        private AssetType assetType;
        private UUID categoryId;
        private String purchasePrice;
        private String currencyCode = "USD";
        private LocalDate purchaseDate;
        private String supplier;
        private String invoiceNumber;
        private String purchaseOrderNumber;
        private String location;
        private String department;
        private String assignedTo;
        private String responsiblePerson;
        private int usefulLifeYears = 5;
        private String depreciationMethod = "STRAIGHT_LINE";
        private String notes;

        public Builder assetId(AssetId assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder assetNumber(String assetNumber) {
            this.assetNumber = assetNumber;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder assetType(AssetType assetType) {
            this.assetType = assetType;
            return this;
        }

        public Builder categoryId(UUID categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public Builder purchasePrice(String purchasePrice) {
            this.purchasePrice = purchasePrice;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder purchaseDate(LocalDate purchaseDate) {
            this.purchaseDate = purchaseDate;
            return this;
        }

        public Builder supplier(String supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder invoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
            return this;
        }

        public Builder purchaseOrderNumber(String purchaseOrderNumber) {
            this.purchaseOrderNumber = purchaseOrderNumber;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder assignedTo(String assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public Builder responsiblePerson(String responsiblePerson) {
            this.responsiblePerson = responsiblePerson;
            return this;
        }

        public Builder usefulLifeYears(int usefulLifeYears) {
            this.usefulLifeYears = usefulLifeYears;
            return this;
        }

        public Builder depreciationMethod(String depreciationMethod) {
            this.depreciationMethod = depreciationMethod;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CreateAssetCommand build() {
            if (assetId == null) {
                assetId = AssetId.generate();
            }
            return new CreateAssetCommand(
                assetId, assetNumber, name, description, assetType,
                categoryId, purchasePrice, currencyCode, purchaseDate,
                supplier, invoiceNumber, purchaseOrderNumber, location,
                department, assignedTo, responsiblePerson, usefulLifeYears,
                depreciationMethod, notes
            );
        }
    }
}