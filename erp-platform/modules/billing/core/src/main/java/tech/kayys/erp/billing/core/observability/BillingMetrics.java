package tech.kayys.erp.billing.core.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Billing metrics for observability.
 */
@ApplicationScoped
public class BillingMetrics {

    @Inject
    MeterRegistry meterRegistry;

    // Counters
    private Counter totalInvoicesGenerated;
    private Counter successfulPayments;
    private Counter failedPayments;
    private Counter refundsProcessed;
    private Counter chargebacksReceived;
    private Counter dunningEvents;
    private Counter billingErrors;

    // Timers
    private Timer invoiceGenerationTimer;
    private Timer paymentProcessingTimer;
    private Timer billingCycleTimer;

    public void initialize() {
        totalInvoicesGenerated = Counter.builder("billing.invoices.total")
            .description("Total number of invoices generated")
            .register(meterRegistry);

        successfulPayments = Counter.builder("billing.payments.success")
            .description("Successful payments")
            .register(meterRegistry);

        failedPayments = Counter.builder("billing.payments.failed")
            .description("Failed payments")
            .register(meterRegistry);

        refundsProcessed = Counter.builder("billing.refunds.total")
            .description("Total refunds processed")
            .register(meterRegistry);

        chargebacksReceived = Counter.builder("billing.chargebacks.total")
            .description("Total chargebacks received")
            .register(meterRegistry);

        dunningEvents = Counter.builder("billing.dunning.events")
            .description("Dunning events triggered")
            .register(meterRegistry);

        billingErrors = Counter.builder("billing.errors.total")
            .description("Total billing errors")
            .register(meterRegistry);

        invoiceGenerationTimer = Timer.builder("billing.invoice.generation.time")
            .description("Time to generate invoice")
            .register(meterRegistry);

        paymentProcessingTimer = Timer.builder("billing.payment.processing.time")
            .description("Time to process payment")
            .register(meterRegistry);

        billingCycleTimer = Timer.builder("billing.cycle.time")
            .description("Time to complete billing cycle")
            .register(meterRegistry);
    }

    public void recordInvoiceGenerated() {
        totalInvoicesGenerated.increment();
    }

    public void recordPaymentSuccess() {
        successfulPayments.increment();
    }

    public void recordPaymentFailure() {
        failedPayments.increment();
    }

    public void recordRefund() {
        refundsProcessed.increment();
    }

    public void recordChargeback() {
        chargebacksReceived.increment();
    }

    public void recordDunningEvent() {
        dunningEvents.increment();
    }

    public void recordError() {
        billingErrors.increment();
    }

    public void recordInvoiceGenerationTime(long duration, TimeUnit unit) {
        invoiceGenerationTimer.record(duration, unit);
    }

    public void recordPaymentProcessingTime(long duration, TimeUnit unit) {
        paymentProcessingTimer.record(duration, unit);
    }

    public void recordBillingCycleTime(long duration, TimeUnit unit) {
        billingCycleTimer.record(duration, unit);
    }

    public <T> T timeInvoiceGeneration(java.util.concurrent.Callable<T> callable) throws Exception {
        return invoiceGenerationTimer.recordCallable(callable);
    }

    public <T> T timePaymentProcessing(java.util.concurrent.Callable<T> callable) throws Exception {
        return paymentProcessingTimer.recordCallable(callable);
    }

    public <T> T timeBillingCycle(java.util.concurrent.Callable<T> callable) throws Exception {
        return billingCycleTimer.recordCallable(callable);
    }
}