package tech.kayys.erp.promotion.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.promotion.domain.identifier.CampaignId;
import tech.kayys.erp.promotion.domain.valueobject.Money;
import tech.kayys.erp.promotion.domain.valueobject.TargetAudience;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Marketing campaign aggregate root.
 * Represents a coordinated marketing effort with multiple promotions.
 */
public final class Campaign extends AggregateRoot<CampaignId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String campaignCode;
    private CampaignType campaignType;
    private CampaignStatus status;
    private TargetAudience targetAudience;
    private List<PromotionId> promotionIds;
    private List<String> channels; // Email, SMS, Social, etc.
    private Money budget;
    private Money spent;
    private Money roi;
    private Instant startDate;
    private Instant endDate;
    private String createdBy;
    private String approvedBy;
    private Instant approvedAt;
    private CampaignMetrics metrics;
    private boolean active;

    private Campaign(CampaignId id) {
        super(id);
        this.promotionIds = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.status = CampaignStatus.DRAFT;
        this.active = true;
        this.metrics = new CampaignMetrics(0, 0, 0, 0, 0);
        this.spent = Money.zero("USD");
        this.roi = Money.zero("USD");
    }

    private Campaign() {
        super();
    }

    /**
     * Factory method to create a new campaign.
     */
    public static Campaign create(
            CampaignId id,
            String name,
            CampaignType campaignType,
            Instant startDate,
            Instant endDate,
            String currencyCode) {
        Campaign campaign = new Campaign(id);
        campaign.name = name;
        campaign.campaignType = campaignType;
        campaign.startDate = startDate;
        campaign.endDate = endDate;
        campaign.budget = Money.zero(currencyCode);
        return campaign;
    }

    /**
     * Adds a promotion to the campaign.
     */
    public void addPromotion(PromotionId promotionId) {
        if (status != CampaignStatus.DRAFT && status != CampaignStatus.PLANNED) {
            throw new IllegalStateException("Cannot modify campaign in status: " + status);
        }
        if (!promotionIds.contains(promotionId)) {
            promotionIds.add(promotionId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a promotion from the campaign.
     */
    public void removePromotion(PromotionId promotionId) {
        if (status != CampaignStatus.DRAFT && status != CampaignStatus.PLANNED) {
            throw new IllegalStateException("Cannot modify campaign in status: " + status);
        }
        promotionIds.remove(promotionId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Launches the campaign.
     */
    public void launch() {
        if (status != CampaignStatus.PLANNED && status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Cannot launch campaign in status: " + status);
        }
        if (promotionIds.isEmpty()) {
            throw new IllegalStateException("Campaign must have at least one promotion");
        }
        
        this.status = CampaignStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Pauses the campaign.
     */
    public void pause() {
        if (status != CampaignStatus.ACTIVE) {
            throw new IllegalStateException("Cannot pause campaign in status: " + status);
        }
        
        this.status = CampaignStatus.PAUSED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Ends the campaign.
     */
    public void end() {
        if (status != CampaignStatus.ACTIVE && status != CampaignStatus.PAUSED) {
            throw new IllegalStateException("Cannot end campaign in status: " + status);
        }
        
        this.status = CampaignStatus.ENDED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records campaign metrics.
     */
    public void recordMetrics(int impressions, int clicks, int conversions, int revenue, int cost) {
        this.metrics = new CampaignMetrics(
            impressions,
            clicks,
            conversions,
            revenue,
            cost
        );
        calculateROI();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates campaign spend.
     */
    public void recordSpend(Money amount) {
        this.spent = spent.add(amount);
        calculateROI();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private void calculateROI() {
        if (spent.isZero()) {
            roi = Money.zero(spent.getCurrency().getCurrencyCode());
            return;
        }
        // Revenue from conversions minus spend
        Money revenue = Money.of(metrics.revenue(), spent.getCurrency().getCurrencyCode());
        roi = revenue.subtract(spent);
    }

    /**
     * Gets the campaign conversion rate.
     */
    public double getConversionRate() {
        if (metrics.clicks() == 0) {
            return 0.0;
        }
        return (double) metrics.conversions() / metrics.clicks() * 100.0;
    }

    /**
     * Gets the campaign ROI percentage.
     */
    public double getROIPercentage() {
        if (spent.isZero()) {
            return 0.0;
        }
        Money revenue = Money.of(metrics.revenue(), spent.getCurrency().getCurrencyCode());
        Money profit = revenue.subtract(spent);
        return profit.getAmount()
            .divide(spent.getAmount(), 4, java.math.RoundingMode.HALF_UP)
            .multiply(java.math.BigDecimal.valueOf(100))
            .doubleValue();
    }

    /**
     * Gets the cost per acquisition.
     */
    public Money getCostPerAcquisition() {
        if (metrics.conversions() == 0) {
            return Money.zero(spent.getCurrency().getCurrencyCode());
        }
        return spent.divide(java.math.BigDecimal.valueOf(metrics.conversions()));
    }

    // Getters and Setters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCampaignCode() { return campaignCode; }
    public CampaignType getCampaignType() { return campaignType; }
    public CampaignStatus getStatus() { return status; }
    public TargetAudience getTargetAudience() { return targetAudience; }
    public List<PromotionId> getPromotionIds() { return Collections.unmodifiableList(promotionIds); }
    public List<String> getChannels() { return Collections.unmodifiableList(channels); }
    public Money getBudget() { return budget; }
    public Money getSpent() { return spent; }
    public Money getRoi() { return roi; }
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    public String getCreatedBy() { return createdBy; }
    public String getApprovedBy() { return approvedBy; }
    public Instant getApprovedAt() { return approvedAt; }
    public CampaignMetrics getMetrics() { return metrics; }
    public boolean isActive() { return active && status == CampaignStatus.ACTIVE; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCampaignCode(String campaignCode) {
        this.campaignCode = campaignCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTargetAudience(TargetAudience targetAudience) {
        this.targetAudience = targetAudience;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setChannels(List<String> channels) {
        this.channels = new ArrayList<>(channels);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBudget(Money budget) {
        this.budget = budget;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void approve(String approvedBy) {
        if (status != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Cannot approve campaign in status: " + status);
        }
        this.approvedBy = approvedBy;
        this.approvedAt = Instant.now();
        this.status = CampaignStatus.PLANNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + campaignType +
                ", status=" + status +
                ", promotions=" + promotionIds.size() +
                '}';
    }

    /**
     * Campaign type enum.
     */
    public enum CampaignType {
        SEASONAL("Seasonal"),
        PRODUCT_LAUNCH("Product Launch"),
        BRAND_AWARENESS("Brand Awareness"),
        LOYALTY("Loyalty"),
        RETENTION("Retention"),
        ACQUISITION("Acquisition"),
        CROSS_SELL("Cross-Sell"),
        UPSELL("Upsell"),
        REFERRAL("Referral");

        private final String displayName;

        CampaignType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Campaign status enum.
     */
    public enum CampaignStatus {
        DRAFT("Draft"),
        PLANNED("Planned"),
        ACTIVE("Active"),
        PAUSED("Paused"),
        ENDED("Ended"),
        CANCELLED("Cancelled");

        private final String displayName;

        CampaignStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public boolean isActive() {
            return this == ACTIVE || this == PLANNED;
        }

        public boolean isTerminal() {
            return this == ENDED || this == CANCELLED;
        }
    }

    /**
     * Campaign metrics record.
     */
    public record CampaignMetrics(
            int impressions,
            int clicks,
            int conversions,
            int revenue,
            int cost
    ) {
        public double getClickThroughRate() {
            if (impressions == 0) {
                return 0.0;
            }
            return (double) clicks / impressions * 100.0;
        }
    }
}