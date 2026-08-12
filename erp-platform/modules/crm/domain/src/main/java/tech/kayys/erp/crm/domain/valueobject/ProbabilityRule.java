package tech.kayys.erp.crm.domain.valueobject;

/**
 * Probability rules for opportunity stages.
 */
public enum ProbabilityRule {
    PROSPECTING(0.1, "Early stage - low probability"),
    QUALIFICATION(0.2, "Qualifying - moderate probability"),
    NEEDS_ANALYSIS(0.3, "Needs identified - increasing probability"),
    PROPOSAL(0.5, "Proposal presented - good probability"),
    NEGOTIATION(0.7, "Negotiating - high probability"),
    CLOSING(0.9, "Closing - very high probability"),
    WON(1.0, "Won - deal closed"),
    LOST(0.0, "Lost - deal closed"),
    ON_HOLD(0.3, "On hold - paused");

    private final double probability;
    private final String description;

    ProbabilityRule(double probability, String description) {
        this.probability = probability;
        this.description = description;
    }

    public double getProbability() {
        return probability;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWinnable() {
        return this != WON && this != LOST;
    }

    public boolean isActive() {
        return this != WON && this != LOST;
    }

    public boolean canTransitionTo(ProbabilityRule target) {
        return switch (this) {
            case PROSPECTING -> target == QUALIFICATION || target == LOST || target == ON_HOLD;
            case QUALIFICATION -> target == NEEDS_ANALYSIS || target == LOST || target == ON_HOLD;
            case NEEDS_ANALYSIS -> target == PROPOSAL || target == LOST || target == ON_HOLD;
            case PROPOSAL -> target == NEGOTIATION || target == LOST || target == ON_HOLD;
            case NEGOTIATION -> target == CLOSING || target == LOST || target == ON_HOLD;
            case CLOSING -> target == WON || target == LOST;
            case ON_HOLD -> target == PROSPECTING || target == QUALIFICATION || 
                           target == NEEDS_ANALYSIS || target == PROPOSAL || 
                           target == NEGOTIATION || target == CLOSING || target == LOST;
            case WON, LOST -> false;
        };
    }
}
// Add these methods to the existing Opportunity class:

/**
 * Moves the opportunity to a new stage with validation.
 */
public void moveToStage(OpportunityStage newStage) {
    if (!stage.canTransitionTo(newStage)) {
        throw new IllegalStateException(
            "Cannot transition from " + stage + " to " + newStage
        );
    }
    
    this.stage = newStage;
    this.probability = newStage.getProbability();
    calculateWeightedValue();
    
    if (newStage == OpportunityStage.WON || newStage == OpportunityStage.LOST) {
        this.active = false;
    }
    
    // Add activity for stage change
    this.addActivity(new OpportunityActivity(
        "STAGE_CHANGE",
        "Moved from " + stage + " to " + newStage,
        "System",
        null
    ));
    
    setUpdatedAt(Instant.now());
    incrementVersion();
}

/**
 * Updates the estimated value.
 */
public void updateValue(double estimatedValue, String currencyCode) {
    if (estimatedValue <= 0) {
        throw new IllegalArgumentException("Estimated value must be positive");
    }
    this.estimatedValue = estimatedValue;
    this.currencyCode = currencyCode;
    calculateWeightedValue();
    setUpdatedAt(Instant.now());
    incrementVersion();
}

/**
 * Gets the time in current stage.
 */
public long getTimeInStageDays() {
    // Would need to track stage entry timestamps
    return 0;
}

/**
 * Gets the opportunity age in days.
 */
public long getAgeDays() {
    if (createdAt == null) {
        return 0;
    }
    return java.time.Duration.between(createdAt, Instant.now()).toDays();
}

/**
 * Checks if the opportunity is at risk.
 */
public boolean isAtRisk() {
    if (stage == OpportunityStage.WON || stage == OpportunityStage.LOST) {
        return false;
    }
    // At risk if in same stage for more than 30 days
    return getTimeInStageDays() > 30;
}

/**
 * Checks if the opportunity is stale.
 */
public boolean isStale() {
    if (stage == OpportunityStage.WON || stage == OpportunityStage.LOST) {
        return false;
    }
    return getAgeDays() > 60;
}