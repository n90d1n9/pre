package tech.kayys.erp.pricing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.pricing.domain.identifier.DynamicPriceRuleId;
import tech.kayys.erp.pricing.domain.valueobject.Money;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Dynamic Price Rule aggregate root.
 * Defines rules for dynamic/real-time pricing based on various factors.
 */
public final class DynamicPriceRule extends AggregateRoot<DynamicPriceRuleId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String productId;
    private String productCategory;
    private List<PriceAdjustment> adjustments;
    private List<PriceTrigger> triggers;
    private RuleType ruleType; // DEMAND, SUPPLY, COMPETITOR, TIME, INVENTORY
    private double basePriceModifier;
    private Money minPrice;
    private Money maxPrice;
    private String currencyCode;
    private boolean active;
    private Instant validFrom;
    private Instant validTo;
    private int priority;
    private String createdBy;
    private String notes;

    private DynamicPriceRule(DynamicPriceRuleId id) {
        super(id);
        this.adjustments = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.active = true;
        this.ruleType = RuleType.DEMAND;
    }

    private DynamicPriceRule() {
        super();
    }

    /**
     * Factory method to create a new dynamic price rule.
     */
    public static DynamicPriceRule create(
            DynamicPriceRuleId id,
            String name,
            String productId,
            RuleType ruleType,
            String currencyCode) {
        DynamicPriceRule rule = new DynamicPriceRule(id);
        rule.name = name;
        rule.productId = productId;
        rule.ruleType = ruleType;
        rule.currencyCode = currencyCode;
        return rule;
    }

    /**
     * Adds a price adjustment to the rule.
     */
    public void addAdjustment(PriceAdjustment adjustment) {
        adjustments.add(adjustment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a price trigger to the rule.
     */
    public void addTrigger(PriceTrigger trigger) {
        triggers.add(trigger);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Calculates the dynamic price based on the rule.
     */
    public Money calculatePrice(Money basePrice, Map<String, Object> context) {
        Money adjustedPrice = basePrice;
        
        // Apply all triggers
        for (PriceTrigger trigger : triggers) {
            if (trigger.isTriggered(context)) {
                adjustedPrice = applyAdjustment(adjustedPrice, trigger.getAdjustment());
            }
        }
        
        // Apply base modifier
        adjustedPrice = adjustedPrice.multiply(
            java.math.BigDecimal.valueOf(1 + basePriceModifier / 100)
        );
        
        // Apply constraints
        if (minPrice != null && adjustedPrice.isLessThan(minPrice)) {
            adjustedPrice = minPrice;
        }
        if (maxPrice != null && adjustedPrice.isGreaterThan(maxPrice)) {
            adjustedPrice = maxPrice;
        }
        
        return adjustedPrice;
    }

    private Money applyAdjustment(Money price, PriceAdjustment adjustment) {
        if (adjustment.getType() == PriceAdjustmentType.PERCENTAGE) {
            double factor = 1 + adjustment.getValue() / 100;
            return price.multiply(java.math.BigDecimal.valueOf(factor));
        } else {
            return price.add(Money.of(adjustment.getValue(), price.getCurrency().getCurrencyCode()));
        }
    }

    /**
     * Checks if the rule is currently active.
     */
    public boolean isActive() {
        if (!active) {
            return false;
        }
        Instant now = Instant.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }
        if (validTo != null && now.isAfter(validTo)) {
            return false;
        }
        return true;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getProductId() { return productId; }
    public String getProductCategory() { return productCategory; }
    public List<PriceAdjustment> getAdjustments() { return Collections.unmodifiableList(adjustments); }
    public List<PriceTrigger> getTriggers() { return Collections.unmodifiableList(triggers); }
    public RuleType getRuleType() { return ruleType; }
    public double getBasePriceModifier() { return basePriceModifier; }
    public Money getMinPrice() { return minPrice; }
    public Money getMaxPrice() { return maxPrice; }
    public String getCurrencyCode() { return currencyCode; }
    public boolean isActive() { return active; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidTo() { return validTo; }
    public int getPriority() { return priority; }
    public String getCreatedBy() { return createdBy; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBasePriceModifier(double basePriceModifier) {
        this.basePriceModifier = basePriceModifier;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMinPrice(Money minPrice) {
        this.minPrice = minPrice;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxPrice(Money maxPrice) {
        this.maxPrice = maxPrice;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setActive(boolean active) {
        this.active = active;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setValidTo(Instant validTo) {
        this.validTo = validTo;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPriority(int priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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
        return "DynamicPriceRule{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", productId='" + productId + '\'' +
                ", ruleType=" + ruleType +
                ", active=" + active +
                '}';
    }

    /**
     * Rule type enum.
     */
    public enum RuleType {
        DEMAND("Demand-based"),
        SUPPLY("Supply-based"),
        COMPETITOR("Competitor-based"),
        TIME("Time-based"),
        INVENTORY("Inventory-based"),
        CUSTOMER("Customer-based");

        private final String description;

        RuleType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Price adjustment value object.
     */
    public static final class PriceAdjustment implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final PriceAdjustmentType type;
        private final double value;
        private final String description;

        public PriceAdjustment(PriceAdjustmentType type, double value, String description) {
            this.type = type;
            this.value = value;
            this.description = description;
            validate();
        }

        @Override
        public void validate() {
            if (type == null) {
                throw new IllegalArgumentException("Adjustment type cannot be null");
            }
        }

        public PriceAdjustmentType getType() { return type; }
        public double getValue() { return value; }
        public String getDescription() { return description; }

        @Override
        public String toString() {
            return "PriceAdjustment{" +
                    "type=" + type +
                    ", value=" + value +
                    '}';
        }
    }

    /**
     * Price adjustment type enum.
     */
    public enum PriceAdjustmentType {
        PERCENTAGE("Percentage"),
        FIXED("Fixed Amount");

        private final String description;

        PriceAdjustmentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Price trigger value object.
     */
    public static final class PriceTrigger implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String condition;
        private final String operator;
        private final double threshold;
        private final PriceAdjustment adjustment;
        private final String description;

        public PriceTrigger(
                String condition,
                String operator,
                double threshold,
                PriceAdjustment adjustment,
                String description) {
            this.condition = condition;
            this.operator = operator;
            this.threshold = threshold;
            this.adjustment = adjustment;
            this.description = description;
            validate();
        }

        @Override
        public void validate() {
            if (condition == null || condition.trim().isEmpty()) {
                throw new IllegalArgumentException("Condition cannot be empty");
            }
            if (operator == null || operator.trim().isEmpty()) {
                throw new IllegalArgumentException("Operator cannot be empty");
            }
            if (adjustment == null) {
                throw new IllegalArgumentException("Adjustment cannot be null");
            }
        }

        public String getCondition() { return condition; }
        public String getOperator() { return operator; }
        public double getThreshold() { return threshold; }
        public PriceAdjustment getAdjustment() { return adjustment; }
        public String getDescription() { return description; }

        public boolean isTriggered(Map<String, Object> context) {
            Object value = context.get(condition);
            if (value == null) {
                return false;
            }
            double doubleValue = ((Number) value).doubleValue();
            
            return switch (operator) {
                case ">" -> doubleValue > threshold;
                case ">=" -> doubleValue >= threshold;
                case "<" -> doubleValue < threshold;
                case "<=" -> doubleValue <= threshold;
                case "==" -> doubleValue == threshold;
                default -> false;
            };
        }

        @Override
        public String toString() {
            return "PriceTrigger{" +
                    "condition='" + condition + '\'' +
                    ", operator='" + operator + '\'' +
                    ", threshold=" + threshold +
                    '}';
        }
    }
}