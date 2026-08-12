package tech.kayys.erp.kiosk.application.api;

import tech.kayys.erp.kiosk.application.api.query.*;
import tech.kayys.erp.kiosk.domain.identifier.KioskId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for kiosk dashboard and monitoring.
 */
public interface KioskDashboardService {

    /**
     * Gets real-time kiosk status.
     */
    CompletionStage<KioskDashboardStatus> getKioskStatus(KioskId kioskId);

    /**
     * Gets all kiosk statuses.
     */
    CompletionStage<List<KioskDashboardStatus>> getAllKioskStatuses();

    /**
     * Gets kiosk performance metrics.
     */
    CompletionStage<KioskPerformanceMetrics> getKioskPerformance(KioskId kioskId, PerformancePeriod period);

    /**
     * Gets kiosk transaction history.
     */
    CompletionStage<KioskTransactionHistory> getTransactionHistory(KioskId kioskId, TransactionHistoryQuery query);

    /**
     * Gets kiosk error logs.
     */
    CompletionStage<KioskErrorLogs> getErrorLogs(KioskId kioskId, ErrorLogQuery query);

    /**
     * Sends a command to a kiosk device.
     */
    CompletionStage<Void> sendKioskCommand(KioskId kioskId, KioskCommand command);

    /**
     * Sends an alert for a kiosk issue.
     */
    CompletionStage<Void> sendKioskAlert(KioskId kioskId, KioskAlert alert);
}