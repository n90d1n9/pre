package tech.kayys.erp.groceries.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.foundation.domain.ValueObject;
import tech.kayys.erp.groceries.domain.identifier.GroceryProductId;
import tech.kayys.erp.groceries.domain.valueobject.GroceryProductType;
import tech.kayys.erp.groceries.domain.valueobject.ShelfLife;
import tech.kayys.erp.groceries.domain.valueobject.Weight;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Grocery Product aggregate root.
 * Extends catalog Product with grocery-specific attributes.
 */
public final class GroceryProduct extends AggregateRoot<GroceryProductId> {

    private static final long serialVersionUID = 1L;

    private UUID catalogProductId;
    private GroceryProductType productType;
    private boolean isWeightBased;
    private Weight defaultWeight;
    private Weight minWeight;
    private Weight maxWeight;
    private ShelfLife shelfLife;
    private boolean requiresTemperatureControl;
    private String temperatureRange;
    private String storageInstructions;
    private String handlingInstructions;
    private List<String> allergens;
    private boolean organic;
    private boolean glutenFree;
    private boolean vegan;
    private boolean kosher;
    private boolean halal;
    private String countryOfOrigin;
    private String supplierBatchNumber;
    private List<BatchLot> batchLots;
    private boolean active;

    private GroceryProduct(GroceryProductId id) {
        super(id);
        this.batchLots = new ArrayList<>();
        this.allergens = new ArrayList<>();
        this.active = true;
        this.isWeightBased = false;
    }

    private GroceryProduct() {
        super();
    }

    public static GroceryProduct create(
            GroceryProductId id,
            UUID catalogProductId,
            GroceryProductType productType) {
        GroceryProduct product = new GroceryProduct(id);
        product.catalogProductId = catalogProductId;
        product.productType = productType;
        product.requiresTemperatureControl = productType.requiresTemperatureControl();
        return product;
    }

    public void addBatchLot(BatchLot batchLot) {
        batchLots.add(batchLot);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void removeBatchLot(String batchNumber) {
        batchLots.removeIf(lot -> lot.getBatchNumber().equals(batchNumber));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void addAllergen(String allergen) {
        if (!allergens.contains(allergen)) {
            allergens.add(allergen);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    public void removeAllergen(String allergen) {
        allergens.remove(allergen);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public boolean isExpired() {
        if (shelfLife == null) {
            return false;
        }
        return shelfLife.isExpired(Instant.now());
    }

    public boolean isExpiringSoon(int daysThreshold) {
        if (shelfLife == null) {
            return false;
        }
        return shelfLife.isExpiringSoon(daysThreshold);
    }

    public UUID getCatalogProductId() { return catalogProductId; }
    public GroceryProductType getProductType() { return productType; }
    public boolean isWeightBased() { return isWeightBased; }
    public Weight getDefaultWeight() { return defaultWeight; }
    public Weight getMinWeight() { return minWeight; }
    public Weight getMaxWeight() { return maxWeight; }
    public ShelfLife getShelfLife() { return shelfLife; }
    public boolean isRequiresTemperatureControl() { return requiresTemperatureControl; }
    public String getTemperatureRange() { return temperatureRange; }
    public String getStorageInstructions() { return storageInstructions; }
    public String getHandlingInstructions() { return handlingInstructions; }
    public List<String> getAllergens() { return Collections.unmodifiableList(allergens); }
    public boolean isOrganic() { return organic; }
    public boolean isGlutenFree() { return glutenFree; }
    public boolean isVegan() { return vegan; }
    public boolean isKosher() { return kosher; }
    public boolean isHalal() { return halal; }
    public String getCountryOfOrigin() { return countryOfOrigin; }
    public String getSupplierBatchNumber() { return supplierBatchNumber; }
    public List<BatchLot> getBatchLots() { return Collections.unmodifiableList(batchLots); }
    public boolean isActive() { return active; }

    public void setWeightBased(boolean weightBased) {
        isWeightBased = weightBased;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDefaultWeight(Weight defaultWeight) {
        this.defaultWeight = defaultWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMinWeight(Weight minWeight) {
        this.minWeight = minWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxWeight(Weight maxWeight) {
        this.maxWeight = maxWeight;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShelfLife(ShelfLife shelfLife) {
        this.shelfLife = shelfLife;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTemperatureRange(String temperatureRange) {
        this.temperatureRange = temperatureRange;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setStorageInstructions(String storageInstructions) {
        this.storageInstructions = storageInstructions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setHandlingInstructions(String handlingInstructions) {
        this.handlingInstructions = handlingInstructions;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setOrganic(boolean organic) {
        this.organic = organic;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setGlutenFree(boolean glutenFree) {
        this.glutenFree = glutenFree;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setKosher(boolean kosher) {
        this.kosher = kosher;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setHalal(boolean halal) {
        this.halal = halal;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setSupplierBatchNumber(String supplierBatchNumber) {
        this.supplierBatchNumber = supplierBatchNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setActive(boolean active) {
        this.active = active;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "GroceryProduct{" +
                "id=" + getId() +
                ", catalogProductId=" + catalogProductId +
                ", productType=" + productType +
                ", isWeightBased=" + isWeightBased +
                '}';
    }

    public static final class BatchLot implements ValueObject {
        private static final long serialVersionUID = 1L;

        private final String batchNumber;
        private final Instant productionDate;
        private final Instant expiryDate;
        private final int quantity;
        private final int remainingQuantity;
        private final String supplierName;
        private final String supplierLotNumber;

        public BatchLot(
                String batchNumber,
                Instant productionDate,
                Instant expiryDate,
                int quantity,
                int remainingQuantity,
                String supplierName,
                String supplierLotNumber) {
            this.batchNumber = batchNumber;
            this.productionDate = productionDate;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.remainingQuantity = remainingQuantity;
            this.supplierName = supplierName;
            this.supplierLotNumber = supplierLotNumber;
            validate();
        }

        @Override
        public void validate() {
            if (batchNumber == null || batchNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Batch number cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (remainingQuantity < 0 || remainingQuantity > quantity) {
                throw new IllegalArgumentException("Invalid remaining quantity");
            }
        }

        public String getBatchNumber() { return batchNumber; }
        public Instant getProductionDate() { return productionDate; }
        public Instant getExpiryDate() { return expiryDate; }
        public int getQuantity() { return quantity; }
        public int getRemainingQuantity() { return remainingQuantity; }
        public String getSupplierName() { return supplierName; }
        public String getSupplierLotNumber() { return supplierLotNumber; }

        public boolean isExpired() {
            return expiryDate != null && Instant.now().isAfter(expiryDate);
        }

        public boolean isExpiringSoon(int daysThreshold) {
            if (expiryDate == null) {
                return false;
            }
            Instant threshold = Instant.now().plusSeconds(daysThreshold * 24L * 60L * 60L);
            return expiryDate.isBefore(threshold);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BatchLot that = (BatchLot) o;
            return Objects.equals(batchNumber, that.batchNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(batchNumber);
        }

        @Override
        public String toString() {
            return "BatchLot{" +
                    "batchNumber='" + batchNumber + '\'' +
                    ", expiryDate=" + expiryDate +
                    ", remainingQuantity=" + remainingQuantity +
                    '}';
        }
    }
}
