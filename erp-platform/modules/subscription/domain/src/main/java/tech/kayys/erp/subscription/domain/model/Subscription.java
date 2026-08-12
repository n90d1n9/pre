package tech.kayys.erp.subscription.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.subscription.domain.event.SubscriptionActivated;
import tech.kayys.erp.subscription.domain.event.SubscriptionCancelled;
import tech.kayys.erp.subscription.domain.event.SubscriptionCreated;
import tech.kayys.erp.subscription.domain.event.SubscriptionPaused;
import tech.kayys.erp.subscription.domain.event.SubscriptionRenewed;
import tech.kayys.erp.subscription.domain.identifier.CustomerId;
import tech.kayys.erp.subscription.domain.identifier.InvoiceId;
import tech.kayys.erp.subscription.domain.identifier.PlanId;
import tech.kayys.erp.subscription.domain.identifier.SubscriptionId;
import tech.kayys.erp.subscription.domain.valueobject.BillingCycle;
import tech.kayys.erp.subscription.domain.valueobject.Money;
import tech.kayys.erp.subscription.domain.valueobject.SubscriptionStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Subscription aggregate root.
 * Represents a customer's subscription to a plan.
 */
public final class Subscription extends AggregateRoot<SubscriptionId> {
    
    private static final long serialVersionUID = 1L;
    
    private CustomerId customerId;
    private PlanId planId;
    private SubscriptionStatus status;
    private BillingCycle billingCycle;
    private Money monthlyFee;
    private Money totalPaid;
    private Instant startDate;
    private Instant nextBillingDate;
    private Instant endDate; // null for ongoing
    private InvoiceId lastInvoiceId;
    private InvoiceId nextInvoiceId;
    private int billingAttempts;
    private boolean autoRenew;
    private String cancellationReason;
    private List<SubscriptionEvent> history;
    private String trialPeriodEnd; // ISO date string
    private boolean inTrial;

    private Subscription(SubscriptionId id) {
        super(id);
        this.status = SubscriptionStatus.DRAFT;
        this.history = new ArrayList<>();
        this.autoRenew = true;
        this.totalPaid = Money.zero("USD");
        this.billingAttempts = 0;
    }

    private Subscription() {
        super();
    }

    /**
     * Factory method to create a new subscription.
     */
    public static Subscription create(
            SubscriptionId id,
            CustomerId customerId,
            PlanId planId,
            Money monthlyFee,
            BillingCycle billingCycle) {
        Subscription subscription = new Subscription(id);
        subscription.customerId = customerId;
        subscription.planId = planId;
        subscription.monthlyFee = monthlyFee;
        subscription.billingCycle = billingCycle;
        subscription.startDate = Instant.now();
        subscription.nextBillingDate = calculateNextBillingDate(Instant.now(), billingCycle);
        subscription.totalPaid = Money.zero(monthlyFee.getCurrency().getCurrencyCode());
        
        subscription.registerEvent(new SubscriptionCreated(subscription));
        return subscription;
    }

    /**
     * Activates the subscription.
     */
    public void activate() {
        if (status != SubscriptionStatus.DRAFT && status != SubscriptionStatus.PAUSED) {
            throw new IllegalStateException("Cannot activate subscription in status: " + status);
        }
        
        this.status = SubscriptionStatus.ACTIVE;
        if (startDate == null) {
            this.startDate = Instant.now();
        }
        if (nextBillingDate == null) {
            this.nextBillingDate = calculateNextBillingDate(Instant.now(), billingCycle);
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionActivated(this));
    }

    /**
     * Pauses the subscription.
     */
    public void pause() {
        if (status != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Cannot pause subscription in status: " + status);
        }
        
        this.status = SubscriptionStatus.PAUSED;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionPaused(this));
    }

    /**
     * Cancels the subscription.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Subscription is already terminated");
        }
        
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationReason = reason;
        this.endDate = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionCancelled(this));
    }

    /**
     * Renews the subscription.
     */
    public void renew() {
        if (status != SubscriptionStatus.ACTIVE && status != SubscriptionStatus.IN_GRACE_PERIOD) {
            throw new IllegalStateException("Cannot renew subscription in status: " + status);
        }
        
        // Move to next billing period
        this.nextBillingDate = calculateNextBillingDate(nextBillingDate, billingCycle);
        this.billingAttempts = 0;
        this.status = SubscriptionStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        registerEvent(new SubscriptionRenewed(this));
    }

    /**
     * Marks payment as received.
     */
    public void recordPayment(InvoiceId invoiceId, Money amount) {
        this.totalPaid = totalPaid.add(amount);
        this.lastInvoiceId = invoiceId;
        this.billingAttempts = 0;
        
        // If in grace period, move back to active
        if (status == SubscriptionStatus.IN_GRACE_PERIOD) {
            this.status = SubscriptionStatus.ACTIVE;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks payment as failed.
     */
    public void recordPaymentFailure() {
        this.billingAttempts++;
        setUpdatedAt(Instant.now());
        incrementVersion();
        
        // After 3 failures, move to grace period
        if (billingAttempts >= 3 && status == SubscriptionStatus.ACTIVE) {
            this.status = SubscriptionStatus.PAST_DUE;
        }
    }

    /**
     * Enters grace period.
     */
    public void enterGracePeriod() {
        if (status != SubscriptionStatus.PAST_DUE) {
            throw new IllegalStateException("Cannot enter grace period from status: " + status);
        }
        this.status = SubscriptionStatus.IN_GRACE_PERIOD;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if subscription needs renewal.
     */
    public boolean needsRenewal() {
        if (!isActive()) {
            return false;
        }
        return Instant.now().isAfter(nextBillingDate);
    }

    /**
     * Checks if subscription is in trial period.
     */
    public boolean isInTrial() {
        return inTrial && 
               (trialPeriodEnd == null || 
                Instant.parse(trialPeriodEnd).isAfter(Instant.now()));
    }

    /**
     * Sets trial period.
     */
    public void setTrialPeriod(int days) {
        this.inTrial = true;
        this.trialPeriodEnd = Instant.now()
            .plusSeconds(days * 24L * 60L * 60L)
            .toString();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private Instant calculateNextBillingDate(Instant from, BillingCycle cycle) {
        LocalDate current = from.atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate next = current.plusMonths(cycle.getMonths());
        return next.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    // Getters
    public CustomerId getCustomerId() { return customerId; }
    public PlanId getPlanId() { return planId; }
    public SubscriptionStatus getStatus() { return status; }
    public BillingCycle getBillingCycle() { return billingCycle; }
    public Money getMonthlyFee() { return monthlyFee; }
    public Money getTotalPaid() { return totalPaid; }
    public Instant getStartDate() { return startDate; }
    public Instant getNextBillingDate() { return nextBillingDate; }
    public Instant getEndDate() { return endDate; }
    public InvoiceId getLastInvoiceId() { return lastInvoiceId; }
    public InvoiceId getNextInvoiceId() { return nextInvoiceId; }
    public int getBillingAttempts() { return billingAttempts; }
    public boolean isAutoRenew() { return autoRenew; }
    public String getCancellationReason() { return cancellationReason; }
    public List<SubscriptionEvent> getHistory() { return Collections.unmodifiableList(history); }
    public boolean isInTrial() { return inTrial; }

    public void setAutoRenew(boolean autoRenew) {
        this.autoRenew = autoRenew;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNextInvoiceId(InvoiceId nextInvoiceId) {
        this.nextInvoiceId = nextInvoiceId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void addHistory(SubscriptionEvent event) {
        history.add(event);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public boolean isActive() {
        return status.isActive() && 
               (endDate == null || Instant.now().isBefore(endDate));
    }

    /**
     * Calculates the prorated amount for a change in plan.
     */
    public Money calculateProratedAmount(SubscriptionPlan newPlan) {
        if (newPlan == null) {
            return Money.zero(monthlyFee.getCurrency().getCurrencyCode());
        }
        
        Money newMonthlyFee = newPlan.getMonthlyPrice();
        int daysRemainingInCycle = calculateDaysRemainingInCycle();
        int totalDaysInCycle = getTotalDaysInCycle();
        
        // Calculate prorated difference
        BigDecimal dailyRateCurrent = monthlyFee.getAmount()
            .divide(BigDecimal.valueOf(totalDaysInCycle), 4, java.math.RoundingMode.HALF_UP);
        BigDecimal dailyRateNew = newMonthlyFee.getAmount()
            .divide(BigDecimal.valueOf(totalDaysInCycle), 4, java.math.RoundingMode.HALF_UP);
        
        BigDecimal difference = dailyRateNew.subtract(dailyRateCurrent)
            .multiply(BigDecimal.valueOf(daysRemainingInCycle));
        
        return Money.of(difference, monthlyFee.getCurrency().getCurrencyCode());
    }

    private int calculateDaysRemainingInCycle() {
        LocalDate now = LocalDate.now();
        LocalDate nextBilling = nextBillingDate.atZone(ZoneId.systemDefault()).toLocalDate();
        long days = java.time.temporal.ChronoUnit.DAYS.between(now, nextBilling);
        return days > 0 ? (int) days : 0;
    }

    private int getTotalDaysInCycle() {
        return switch (billingCycle) {
            case MONTHLY -> 30;
            case QUARTERLY -> 90;
            case SEMI_ANNUAL -> 180;
            case ANNUAL -> 365;
            case BIENNIAL -> 730;
            case TRIENNIAL -> 1095;
        };
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + getId() +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", status=" + status +
                ", monthlyFee=" + monthlyFee +
                ", nextBillingDate=" + nextBillingDate +
                '}';
    }

    /**
     * Subscription event record for history.
     */
    public record SubscriptionEvent(
            String type,
            Instant timestamp,
            String description,
            String details
    ) {}
}