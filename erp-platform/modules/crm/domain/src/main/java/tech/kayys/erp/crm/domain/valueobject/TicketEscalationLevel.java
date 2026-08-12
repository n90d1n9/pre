package tech.kayys.erp.crm.domain.valueobject;

/**
 * Escalation levels for support tickets.
 */
public enum TicketEscalationLevel {
    LEVEL_1("Level 1 - First line support"),
    LEVEL_2("Level 2 - Second line support"),
    LEVEL_3("Level 3 - Third line support"),
    LEVEL_4("Level 4 - Management escalation"),
    LEVEL_5("Level 5 - Executive escalation");

    private final String description;

    TicketEscalationLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return switch (this) {
            case LEVEL_1 -> 1;
            case LEVEL_2 -> 2;
            case LEVEL_3 -> 3;
            case LEVEL_4 -> 4;
            case LEVEL_5 -> 5;
        };
    }
}
// Add these fields and methods to the existing SupportTicket class:

public final class SupportTicket extends AggregateRoot<TicketId> {
    // ... existing fields ...
    
    private SLAStatus slaStatus;
    private TicketEscalationLevel escalationLevel;
    private int escalationCount;
    private double slaResponseHours;
    private double slaResolutionHours;
    private Instant firstResponseAt;
    private Instant lastResponseAt;
    private int responseCount;
    private int internalResponseCount;
    private String satisfactionRating;
    private String satisfactionComment;
    private String timeToFirstResponse;
    private String timeToResolution;
    
    // ... existing constructor ...
    
    /**
     * Tracks the SLA status.
     */
    public void trackSLA() {
        if (status == TicketStatus.CLOSED || status == TicketStatus.RESOLVED) {
            this.slaStatus = SLAStatus.WITHIN_SLA;
            return;
        }
        
        Instant now = Instant.now();
        long hoursSinceCreation = java.time.Duration.between(createdAt, now).toHours();
        
        if (hoursSinceCreation > slaResolutionHours) {
            this.slaStatus = SLAStatus.BREACHED;
        } else if (hoursSinceCreation > slaResolutionHours * 0.7) {
            this.slaStatus = SLAStatus.AT_RISK;
        } else {
            this.slaStatus = SLAStatus.WITHIN_SLA;
        }
        
        setUpdatedAt(now);
        incrementVersion();
    }
    
    /**
     * Updates the escalation level based on severity and duration.
     */
    public void updateEscalation() {
        if (status == TicketStatus.CLOSED || status == TicketStatus.RESOLVED) {
            return;
        }
        
        Instant now = Instant.now();
        long hoursSinceCreation = java.time.Duration.between(createdAt, now).toHours();
        
        TicketEscalationLevel newLevel = escalationLevel;
        
        // Escalate based on priority and time
        if (priority == TicketPriority.CRITICAL && hoursSinceCreation > 1) {
            newLevel = TicketEscalationLevel.LEVEL_3;
        } else if (priority == TicketPriority.HIGH && hoursSinceCreation > 4) {
            newLevel = TicketEscalationLevel.LEVEL_2;
        } else if (hoursSinceCreation > 24) {
            newLevel = TicketEscalationLevel.LEVEL_2;
        } else if (hoursSinceCreation > 48) {
            newLevel = TicketEscalationLevel.LEVEL_3;
        } else if (hoursSinceCreation > 72) {
            newLevel = TicketEscalationLevel.LEVEL_4;
        }
        
        if (newLevel != escalationLevel) {
            this.escalationLevel = newLevel;
            this.escalationCount++;
            setUpdatedAt(now);
            incrementVersion();
        }
    }
    
    /**
     * Records a response from the agent.
     */
    public void recordResponse(String agentId, boolean internal) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot respond to closed ticket");
        }
        
        this.lastResponseAt = Instant.now();
        this.responseCount++;
        if (internal) {
            this.internalResponseCount++;
        }
        
        if (firstResponseAt == null) {
            this.firstResponseAt = Instant.now();
            this.timeToFirstResponse = calculateTimeToFirstResponse();
        }
        
        if (status == TicketStatus.NEW || status == TicketStatus.ASSIGNED) {
            this.status = TicketStatus.IN_PROGRESS;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    /**
     * Records a customer response.
     */
    public void recordCustomerResponse() {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Cannot respond to closed ticket");
        }
        
        this.lastResponseAt = Instant.now();
        this.status = TicketStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    /**
     * Sets SLA expectations based on priority.
     */
    public void setSLAExpectations(TicketPriority priority) {
        this.priority = priority;
        this.slaResponseHours = switch (priority) {
            case CRITICAL -> 1.0;
            case HIGH -> 4.0;
            case MEDIUM -> 8.0;
            case LOW -> 24.0;
            case TRIVIAL -> 48.0;
        };
        this.slaResolutionHours = switch (priority) {
            case CRITICAL -> 4.0;
            case HIGH -> 24.0;
            case MEDIUM -> 48.0;
            case LOW -> 72.0;
            case TRIVIAL -> 120.0;
        };
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    /**
     * Records satisfaction rating.
     */
    public void recordSatisfaction(String rating, String comment) {
        this.satisfactionRating = rating;
        this.satisfactionComment = comment;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }
    
    private String calculateTimeToFirstResponse() {
        if (firstResponseAt == null) {
            return null;
        }
        long minutes = java.time.Duration.between(createdAt, firstResponseAt).toMinutes();
        return minutes + " minutes";
    }
    
    /**
     * Calculates time to resolution.
     */
    public void calculateTimeToResolution() {
        if (resolvedAt != null) {
            long minutes = java.time.Duration.between(createdAt, resolvedAt).toMinutes();
            this.timeToResolution = minutes + " minutes";
        }
    }
    
    // Additional getters
    public SLAStatus getSlaStatus() { return slaStatus; }
    public TicketEscalationLevel getEscalationLevel() { return escalationLevel; }
    public int getEscalationCount() { return escalationCount; }
    public double getSlaResponseHours() { return slaResponseHours; }
    public double getSlaResolutionHours() { return slaResolutionHours; }
    public Instant getFirstResponseAt() { return firstResponseAt; }
    public Instant getLastResponseAt() { return lastResponseAt; }
    public int getResponseCount() { return responseCount; }
    public int getInternalResponseCount() { return internalResponseCount; }
    public String getSatisfactionRating() { return satisfactionRating; }
    public String getSatisfactionComment() { return satisfactionComment; }
    public String getTimeToFirstResponse() { return timeToFirstResponse; }
    public String getTimeToResolution() { return timeToResolution; }
    
    // Update toString
    @Override
    public String toString() {
        return "SupportTicket{" +
                "id=" + getId() +
                ", ticketNumber='" + ticketNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", escalationLevel=" + escalationLevel +
                ", slaStatus=" + slaStatus +
                ", customerName='" + customerName + '\'' +
                '}';
    }
}