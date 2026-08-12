package tech.kayys.erp.billing.application.service;

import tech.kayys.erp.billing.domain.valueobject.Money;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Billing reconciliation service.
 * Matches billing records with accounting entries.
 */
@Singleton
public class ReconciliationService {

    /**
     * Performs reconciliation between billing and accounting.
     */
    public CompletionStage<ReconciliationResult> reconcile(
            Instant startDate,
            Instant endDate,
            String customerId) {
        
        // In production, this would query both billing and accounting data
        List<ReconciliationItem> items = new ArrayList<>();
        // Mock data
        items.add(new ReconciliationItem(
            "INV-001",
            "INV-001",
            Money.of("100.00", "USD"),
            "MATCHED",
            "No discrepancy"
        ));
        
        Money totalBilled = Money.of("100.00", "USD");
        Money totalAccounted = Money.of("100.00", "USD");
        Money discrepancy = totalBilled.subtract(totalAccounted);
        
        return CompletableFuture.completedFuture(
            new ReconciliationResult(
                startDate,
                endDate,
                customerId,
                items,
                totalBilled,
                totalAccounted,
                discrepancy,
                discrepancy.isZero(),
                "Reconciliation complete",
                Instant.now()
            )
        );
    }

    /**
     * Reconciliation result record.
     */
    public record ReconciliationResult(
            Instant startDate,
            Instant endDate,
            String customerId,
            List<ReconciliationItem> items,
            Money totalBilled,
            Money totalAccounted,
            Money discrepancy,
            boolean reconciled,
            String notes,
            Instant processedAt
    ) {}

    /**
     * Reconciliation item record.
     */
    public record ReconciliationItem(
            String billingId,
            String accountingId,
            Money amount,
            String status,
            String notes
    ) {}
}