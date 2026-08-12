package tech.kayys.erp.catalog.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.Objects;

/**
 * Product lifecycle value object.
 * Tracks the product through its entire lifecycle stages.
 */
public final class ProductLifecycle implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final LifecycleStage stage;
    private final Instant stageStartDate;
    private final Instant stageEndDate;
    private final String notes;
    private final String lastModifiedBy;

    public ProductLifecycle(
            LifecycleStage stage,
            Instant stageStartDate,
            Instant stageEndDate,
            String notes,
            String lastModifiedBy) {
        this.stage = stage;
        this.stageStartDate = stageStartDate;
        this.stageEndDate = stageEndDate;
        this.notes = notes;
        this.lastModifiedBy = lastModifiedBy;
        validate();
    }

    @Override
    public void validate() {
        if (stage == null) {
            throw new IllegalArgumentException("Lifecycle stage cannot be null");
        }
        if (stageStartDate == null) {
            throw new IllegalArgumentException("Stage start date cannot be null");
        }
        if (stageEndDate != null && stageEndDate.isBefore(stageStartDate)) {
            throw new IllegalArgumentException("Stage end date must be after start date");
        }
    }

    public LifecycleStage getStage() { return stage; }
    public Instant getStageStartDate() { return stageStartDate; }
    public Instant getStageEndDate() { return stageEndDate; }
    public String getNotes() { return notes; }
    public String getLastModifiedBy() { return lastModifiedBy; }

    public boolean isInDevelopment() {
        return stage == LifecycleStage.DEVELOPMENT;
    }

    public boolean isActive() {
        return stage == LifecycleStage.ACTIVE;
    }

    public boolean isEndOfLife() {
        return stage == LifecycleStage.EOL || stage == LifecycleStage.DISCONTINUED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductLifecycle that = (ProductLifecycle) o;
        return stage == that.stage &&
               Objects.equals(stageStartDate, that.stageStartDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stage, stageStartDate);
    }

    @Override
    public String toString() {
        return "ProductLifecycle{" +
                "stage=" + stage +
                ", stageStartDate=" + stageStartDate +
                '}';
    }

    /**
     * Lifecycle stage enum.
     */
    public enum LifecycleStage {
        CONCEPT("Concept - Idea stage"),
        DEVELOPMENT("Development - Being created"),
        BETA("Beta - Testing phase"),
        LAUNCH("Launch - Going to market"),
        ACTIVE("Active - Full production"),
        MATURITY("Maturity - Established product"),
        DECLINE("Decline - Decreasing sales"),
        EOL("End of Life - Planned obsolescence"),
        DISCONTINUED("Discontinued - No longer sold");

        private final String description;

        LifecycleStage(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public boolean canTransitionTo(LifecycleStage target) {
            return switch (this) {
                case CONCEPT -> target == DEVELOPMENT || target == DISCONTINUED;
                case DEVELOPMENT -> target == BETA || target == DISCONTINUED;
                case BETA -> target == LAUNCH || target == DISCONTINUED;
                case LAUNCH -> target == ACTIVE || target == DISCONTINUED;
                case ACTIVE -> target == MATURITY || target == EOL || target == DISCONTINUED;
                case MATURITY -> target == DECLINE || target == EOL || target == DISCONTINUED;
                case DECLINE -> target == EOL || target == DISCONTINUED;
                case EOL, DISCONTINUED -> false;
            };
        }

        public boolean isPreLaunch() {
            return this == CONCEPT || this == DEVELOPMENT || this == BETA;
        }

        public boolean isPostLaunch() {
            return this == ACTIVE || this == MATURITY || this == DECLINE;
        }
    }

    public static ProductLifecycle initial() {
        return new ProductLifecycle(
            LifecycleStage.CONCEPT,
            Instant.now(),
            null,
            "Product created",
            "System"
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LifecycleStage stage;
        private Instant stageStartDate;
        private Instant stageEndDate;
        private String notes;
        private String lastModifiedBy;

        public Builder stage(LifecycleStage stage) {
            this.stage = stage;
            return this;
        }

        public Builder stageStartDate(Instant stageStartDate) {
            this.stageStartDate = stageStartDate;
            return this;
        }

        public Builder stageEndDate(Instant stageEndDate) {
            this.stageEndDate = stageEndDate;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder lastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
            return this;
        }

        public ProductLifecycle build() {
            if (stageStartDate == null) {
                stageStartDate = Instant.now();
            }
            return new ProductLifecycle(stage, stageStartDate, stageEndDate, notes, lastModifiedBy);
        }
    }
}
// Add these fields to the existing Product class:

public final class Product extends AggregateRoot<ProductId> {
    // ... existing fields ...
    
    // New fields for missing components
    private CategoryId categoryId;
    private List<CategoryId> additionalCategoryIds;
    private String brand;
    private String manufacturer;
    private String manufacturerPartNumber;
    private String upc;
    private String ean;
    private String isbn;
    private String mpn;
    private String productType; // PHYSICAL, DIGITAL, SERVICE, SUBSCRIPTION
    private boolean taxable;
    private String taxCode;
    private boolean shippable;
    private boolean virtual;
    private boolean downloadable;
    private String downloadUrl;
    private String downloadFileHash;
    private ProductLifecycle lifecycle;
    private List<ProductVariation> variations;
    private List<ProductMedia> media;
    private List<ProductReviewSummary> reviewSummary;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String h1Tag;
    private String canonicalUrl;
    private boolean featured;
    private boolean newArrival;
    private boolean bestSeller;
    private Instant featuredUntil;
    private double weight;
    private double width;
    private double height;
    private double depth;
    private String weightUnit; // KG, LB
    private String dimensionUnit; // CM, IN
    private String shippingClass;
    private int minOrderQuantity;
    private int maxOrderQuantity;
    private String warrantyInformation;
    private String returnPolicy;
    private String safetyWarnings;
    private List<String> relatedProductIds;
    private List<String> upsellingProductIds;
    private List<String> crossSellingProductIds;
    private String supplierId;
    private String supplierSku;
    private int leadTimeDays;
    private int reorderPoint;
    private int reorderQuantity;
    private String inventoryTracking; // NONE, SIMPLE, SERIAL, LOT
    private boolean allowBackorders;
    private String backorderMessage;
    private String inventoryNotes;
    private String seoTitle;
    private String seoDescription;
    private String ogTitle;
    private String ogDescription;
    private String ogImageUrl;
    private String twitterCard;
    private String twitterTitle;
    private String twitterDescription;
    private String twitterImageUrl;
    private String schemaMarkup;
    private String sourceId; // For imports
    private String sourceSystem;
    private Instant sourceCreatedAt;
    private Instant sourceUpdatedAt;
    private boolean deleted;
    private Instant deletedAt;
    private String deletedReason;

    // ... existing constructor and methods ...
    
    // Add getters and setters for all new fields
    public CategoryId getCategoryId() { return categoryId; }
    public void setCategoryId(CategoryId categoryId) { this.categoryId = categoryId; }
    
    public List<CategoryId> getAdditionalCategoryIds() { 
        return Collections.unmodifiableList(additionalCategoryIds); 
    }
    public void setAdditionalCategoryIds(List<CategoryId> additionalCategoryIds) {
        this.additionalCategoryIds = new ArrayList<>(additionalCategoryIds);
    }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    
    public String getManufacturerPartNumber() { return manufacturerPartNumber; }
    public void setManufacturerPartNumber(String manufacturerPartNumber) { 
        this.manufacturerPartNumber = manufacturerPartNumber; 
    }
    
    public String getUpc() { return upc; }
    public void setUpc(String upc) { this.upc = upc; }
    
    public String getEan() { return ean; }
    public void setEan(String ean) { this.ean = ean; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public String getMpn() { return mpn; }
    public void setMpn(String mpn) { this.mpn = mpn; }
    
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
    
    public boolean isTaxable() { return taxable; }
    public void setTaxable(boolean taxable) { this.taxable = taxable; }
    
    public String getTaxCode() { return taxCode; }
    public void setTaxCode(String taxCode) { this.taxCode = taxCode; }
    
    public boolean isShippable() { return shippable; }
    public void setShippable(boolean shippable) { this.shippable = shippable; }
    
    public boolean isVirtual() { return virtual; }
    public void setVirtual(boolean virtual) { this.virtual = virtual; }
    
    public boolean isDownloadable() { return downloadable; }
    public void setDownloadable(boolean downloadable) { this.downloadable = downloadable; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    
    public String getDownloadFileHash() { return downloadFileHash; }
    public void setDownloadFileHash(String downloadFileHash) { this.downloadFileHash = downloadFileHash; }
    
    public ProductLifecycle getLifecycle() { return lifecycle; }
    public void setLifecycle(ProductLifecycle lifecycle) { this.lifecycle = lifecycle; }
    
    public List<ProductVariation> getVariations() { 
        return Collections.unmodifiableList(variations); 
    }
    public void setVariations(List<ProductVariation> variations) {
        this.variations = new ArrayList<>(variations);
    }
    
    public List<ProductMedia> getMedia() { 
        return Collections.unmodifiableList(media); 
    }
    public void setMedia(List<ProductMedia> media) {
        this.media = new ArrayList<>(media);
    }
    
    public void addMedia(ProductMedia mediaItem) {
        if (this.media == null) {
            this.media = new ArrayList<>();
        }
        this.media.add(mediaItem);
    }
    
    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }
    
    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }
    
    public String getMetaKeywords() { return metaKeywords; }
    public void setMetaKeywords(String metaKeywords) { this.metaKeywords = metaKeywords; }
    
    // ... continue with all remaining getters and setters ...
}
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule catalogDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.catalog.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog.domain.model..",
                        "tech.kayys.erp.catalog.domain.identifier..",
                        "tech.kayys.erp.catalog.domain.valueobject..",
                        "tech.kayys.erp.catalog.domain.event..",
                        "tech.kayys.erp.catalog.domain.repository.."
                );

@ArchTest
static final ArchRule catalogProductShouldHaveLifecycle =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.catalog.domain.model..")
                .and()
                .haveSimpleName("Product")
                .should()
                .haveField("lifecycle");

@ArchTest
static final ArchRule catalogVariationsShouldBeValueObjects =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.catalog.domain.model..")
                .and()
                .haveSimpleName("ProductVariation")
                .should()
                .beFinal()
                .andShould()
                .implement(tech.kayys.erp.foundation.domain.ValueObject.class);