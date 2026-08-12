package tech.kayys.erp.billing.core.batching;

import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Billing batch processor.
 * Processes billing operations in batches for efficiency.
 */
@ApplicationScoped
public class BillingBatchProcessor {

    private static final Logger log = LoggerFactory.getLogger(BillingBatchProcessor.class);

    @Inject
    BillingScheduleRepository billingScheduleRepository;

    @Inject
    InvoiceRepository invoiceRepository;

    @Inject
    TransactionRepository transactionRepository;

    private static final int BATCH_SIZE = 100;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * Scheduled batch processing of due bills.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public Uni<Void> processDueBillings() {
        log.info("Starting batch billing processing");

        return billingScheduleRepository.findDueSchedules()
            .onItem()
            .transformToUni(schedules -> {
                if (schedules.isEmpty()) {
                    log.info("No due billing schedules found");
                    return Uni.createFrom().voidItem();
                }

                List<Uni<BillingResult>> batchResults = new ArrayList<>();
                List<BillingSchedule> batch = new ArrayList<>();

                for (int i = 0; i < schedules.size(); i++) {
                    batch.add(schedules.get(i));
                    
                    if (batch.size() >= BATCH_SIZE || i == schedules.size() - 1) {
                        final List<BillingSchedule> currentBatch = new ArrayList<>(batch);
                        batchResults.add(processBatch(currentBatch));
                        batch.clear();
                    }
                }

                return Uni.combine()
                    .all()
                    .unis(batchResults)
                    .combinedWith(results -> {
                        long success = results.stream()
                            .filter(r -> r.isSuccess())
                            .count();
                        long failed = results.size() - success;
                        log.info("Batch processing completed: {} success, {} failed", success, failed);
                        return null;
                    });
            });
    }

    private Uni<BillingResult> processBatch(List<BillingSchedule> schedules) {
        return Uni.createFrom()
            .deferred(() -> {
                List<Uni<Void>> batchOperations = new ArrayList<>();
                BillingResult result = new BillingResult();

                for (BillingSchedule schedule : schedules) {
                    // Process each schedule with retry
                    batchOperations.add(
                        processWithRetry(schedule)
                            .onItem()
                            .transform(v -> null)
                    );
                }

                return Uni.combine()
                    .all()
                    .unis(batchOperations)
                    .combinedWith(v -> {
                        result.setSuccess(true);
                        result.setProcessedCount(schedules.size());
                        return result;
                    })
                    .onFailure()
                    .recoverWithItem(throwable -> {
                        log.error("Batch processing failed", throwable);
                        result.setSuccess(false);
                        result.setErrorMessage(throwable.getMessage());
                        return result;
                    });
            });
    }

    private Uni<BillingSchedule> processWithRetry(BillingSchedule schedule) {
        return Uni.createFrom()
            .deferred(() -> processSingleBilling(schedule))
            .onFailure()
            .retry()
            .withBackOff(Duration.ofSeconds(5), Duration.ofMinutes(1))
            .atMost(MAX_RETRY_ATTEMPTS)
            .onFailure()
            .recoverWithItem(throwable -> {
                log.error("Failed to process billing schedule after {} attempts: {}", 
                    MAX_RETRY_ATTEMPTS, schedule.getId(), throwable);
                schedule.markFailed(throwable.getMessage());
                return schedule;
            });
    }

    private Uni<BillingSchedule> processSingleBilling(BillingSchedule schedule) {
        // In production, this would call the BillingOrchestrator
        return Uni.createFrom().item(schedule);
    }

    /**
     * Billing result record.
     */
    public static class BillingResult {
        private boolean success;
        private int processedCount;
        private String errorMessage;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getProcessedCount() { return processedCount; }
        public void setProcessedCount(int processedCount) { this.processedCount = processedCount; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
}