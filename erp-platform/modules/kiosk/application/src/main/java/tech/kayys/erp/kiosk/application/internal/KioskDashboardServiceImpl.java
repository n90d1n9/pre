package tech.kayys.erp.kiosk.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.kiosk.application.api.KioskDashboardService;
import tech.kayys.erp.kiosk.domain.model.KioskDevice;
import tech.kayys.erp.kiosk.domain.model.KioskSession;
import tech.kayys.erp.kiosk.domain.repository.KioskDeviceRepository;
import tech.kayys.erp.kiosk.domain.repository.KioskSessionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Implementation of kiosk dashboard service.
 */
@Singleton
@UseCase("Kiosk dashboard and monitoring")
public class KioskDashboardServiceImpl implements KioskDashboardService {

    private final KioskDeviceRepository deviceRepository;
    private final KioskSessionRepository sessionRepository;

    @Inject
    public KioskDashboardServiceImpl(
            KioskDeviceRepository deviceRepository,
            KioskSessionRepository sessionRepository) {
        this.deviceRepository = deviceRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public CompletionStage<KioskDashboardStatus> getKioskStatus(KioskId kioskId) {
        return deviceRepository.findById(kioskId)
            .thenCompose(deviceOpt -> {
                if (deviceOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Kiosk not found: " + kioskId)
                    );
                }

                KioskDevice device = deviceOpt.get();
                
                return sessionRepository.findActiveSessions()
                    .thenApply(activeSessions -> {
                        List<KioskSession> sessionList = activeSessions.stream()
                            .filter(s -> s.getKioskId().equals(kioskId.getValue()))
                            .collect(Collectors.toList());

                        return new KioskDashboardStatus(
                            device.getId().toString(),
                            device.getDeviceName(),
                            device.getLocation(),
                            device.getStatus().name(),
                            device.isActive(),
                            device.getMode().name(),
                            device.getCashDrawerBalance(),
                            device.getThermalPaperRemaining(),
                            device.getReceiptPaperRemaining(),
                            sessionList.size(),
                            device.getAverageSessionDurationMinutes(),
                            device.getEvents().stream()
                                .filter(e -> "ERROR".equals(e.getSeverity()) || "CRITICAL".equals(e.getSeverity()))
                                .limit(5)
                                .collect(Collectors.toList()),
                            Instant.now()
                        );
                    });
            });
    }

    @Override
    public CompletionStage<List<KioskDashboardStatus>> getAllKioskStatuses() {
        return deviceRepository.findAll()
            .thenCompose(devices -> {
                List<CompletableFuture<KioskDashboardStatus>> futures = devices.stream()
                    .map(device -> getKioskStatus(device.getId())
                        .toCompletableFuture()
                    )
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
                    );
            });
    }

    @Override
    public CompletionStage<KioskPerformanceMetrics> getKioskPerformance(
            KioskId kioskId, 
            PerformancePeriod period) {
        
        Instant startDate = calculateStartDate(period);
        
        return deviceRepository.findById(kioskId)
            .thenCompose(deviceOpt -> {
                if (deviceOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Kiosk not found: " + kioskId)
                    );
                }

                KioskDevice device = deviceOpt.get();
                
                return sessionRepository.findByKioskAndDateRange(kioskId, startDate, Instant.now())
                    .thenApply(sessions -> {
                        long totalSessions = sessions.size();
                        long completedSessions = sessions.stream()
                            .filter(s -> s.getStatus() == SessionStatus.COMPLETED)
                            .count();
                        long abandonedSessions = sessions.stream()
                            .filter(s -> s.getStatus() == SessionStatus.ABANDONED)
                            .count();
                        
                        double avgDuration = sessions.stream()
                            .filter(s -> s.getEndedAt() != null)
                            .mapToLong(KioskSession::getDurationSeconds)
                            .average()
                            .orElse(0.0) / 60.0;
                        
                        double conversionRate = totalSessions > 0 ? 
                            (double) completedSessions / totalSessions * 100 : 0.0;
                        
                        long itemsScanned = sessions.stream()
                            .mapToLong(KioskSession::getItemsScanned)
                            .sum();
                        
                        double avgItemsPerSession = totalSessions > 0 ? 
                            (double) itemsScanned / totalSessions : 0.0;
                        
                        return new KioskPerformanceMetrics(
                            kioskId.toString(),
                            device.getDeviceName(),
                            period.name(),
                            totalSessions,
                            completedSessions,
                            abandonedSessions,
                            conversionRate,
                            avgDuration,
                            avgItemsPerSession,
                            startDate,
                            Instant.now()
                        );
                    });
            });
    }

    @Override
    public CompletionStage<KioskTransactionHistory> getTransactionHistory(
            KioskId kioskId, 
            TransactionHistoryQuery query) {
        // Implementation would fetch transaction history
        return CompletableFuture.completedFuture(
            new KioskTransactionHistory(
                kioskId.toString(),
                List.of(),
                0,
                0,
                0,
                false,
                false
            )
        );
    }

    @Override
    public CompletionStage<KioskErrorLogs> getErrorLogs(
            KioskId kioskId, 
            ErrorLogQuery query) {
        // Implementation would fetch error logs
        return CompletableFuture.completedFuture(
            new KioskErrorLogs(
                kioskId.toString(),
                List.of(),
                query.getSeverity(),
                query.getFromDate(),
                query.getToDate()
            )
        );
    }

    @Override
    public CompletionStage<Void> sendKioskCommand(KioskId kioskId, KioskCommand command) {
        // Implementation would send commands to the kiosk hardware
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> sendKioskAlert(KioskId kioskId, KioskAlert alert) {
        // Implementation would send alerts (email, SMS, dashboard notification)
        return CompletableFuture.completedFuture(null);
    }

    private Instant calculateStartDate(PerformancePeriod period) {
        return switch (period) {
            case TODAY -> Instant.now().minusSeconds(24 * 60 * 60);
            case YESTERDAY -> Instant.now().minusSeconds(2 * 24 * 60 * 60);
            case WEEK -> Instant.now().minusSeconds(7 * 24 * 60 * 60);
            case MONTH -> Instant.now().minusSeconds(30 * 24 * 60 * 60);
            case YEAR -> Instant.now().minusSeconds(365 * 24 * 60 * 60);
            case ALL_TIME -> Instant.EPOCH;
        };
    }

    /**
     * Dashboard status DTO.
     */
    public record KioskDashboardStatus(
            String kioskId,
            String deviceName,
            String location,
            String status,
            boolean active,
            String mode,
            String cashDrawerBalance,
            int thermalPaperRemaining,
            int receiptPaperRemaining,
            int activeSessions,
            double avgSessionDuration,
            List<KioskDevice.KioskEvent> recentErrors,
            Instant timestamp
    ) {}

    /**
     * Performance metrics DTO.
     */
    public record KioskPerformanceMetrics(
            String kioskId,
            String deviceName,
            String period,
            long totalSessions,
            long completedSessions,
            long abandonedSessions,
            double conversionRate,
            double avgDurationMinutes,
            double avgItemsPerSession,
            Instant periodStart,
            Instant periodEnd
    ) {}

    /**
     * Performance period enum.
     */
    public enum PerformancePeriod {
        TODAY, YESTERDAY, WEEK, MONTH, YEAR, ALL_TIME
    }

    /**
     * Kiosk commands.
     */
    public enum KioskCommand {
        REBOOT, SHUTDOWN, RESTART_SOFTWARE, PRINT_TEST, OPEN_CASH_DRAWER,
        CALIBRATE_SCREEN, TEST_SCANNER, TEST_SCALE, RESET_SESSION
    }

    /**
     * Kiosk alert types.
     */
    public enum KioskAlert {
        LOW_PAPER("Low Paper"), 
        LOW_THERMAL("Low Thermal Paper"),
        LOW_CASH("Low Cash"), 
        ERROR("Error"), 
        MAINTENANCE_NEEDED("Maintenance Needed"),
        STUCK_SESSION("Stuck Session");

        private final String displayName;

        KioskAlert(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}