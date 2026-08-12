package tech.kayys.erp.kiosk.application.internal;

import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.kiosk.domain.model.KioskSession;
import tech.kayys.erp.kiosk.domain.repository.KioskSessionRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Kiosk session manager for handling idle timeouts and session cleanup.
 */
@Singleton
@UseCase("Manage kiosk sessions")
public class KioskSessionManager {

    private final KioskSessionRepository sessionRepository;
    private final ScheduledExecutorService scheduler;

    @Inject
    public KioskSessionManager(KioskSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Starts the session monitor.
     */
    public void startSessionMonitor() {
        scheduler.scheduleAtFixedRate(
            this::checkIdleSessions,
            0,
            30,
            TimeUnit.SECONDS
        );
    }

    /**
     * Checks for idle sessions and handles timeouts.
     */
    public void checkIdleSessions() {
        sessionRepository.findActiveSessions()
            .thenCompose(sessions -> {
                List<CompletableFuture<Void>> futures = sessions.stream()
                    .filter(this::isSessionIdle)
                    .map(session -> handleSessionTimeout(session)
                        .toCompletableFuture()
                    )
                    .collect(Collectors.toList());

                return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> null);
            });
    }

    private boolean isSessionIdle(KioskSession session) {
        long idleSeconds = java.time.Duration.between(
            session.getLastActivityAt(),
            Instant.now()
        ).getSeconds();
        return idleSeconds > 300; // 5 minutes idle timeout
    }

    private CompletionStage<Void> handleSessionTimeout(KioskSession session) {
        session.abandon();
        return sessionRepository.save(session)
            .thenApply(v -> null);
    }

    /**
     * Gets session statistics.
     */
    public CompletionStage<SessionStatistics> getSessionStatistics() {
        return sessionRepository.findActiveSessions()
            .thenApply(activeSessions -> {
                long totalSessions = sessionRepository.countAll();
                long abandonedSessions = sessionRepository.countByStatus(SessionStatus.ABANDONED);
                long completedSessions = sessionRepository.countByStatus(SessionStatus.COMPLETED);
                
                double avgDuration = sessionRepository.getAverageSessionDuration();
                double conversionRate = totalSessions > 0 ? 
                    (double) completedSessions / totalSessions * 100 : 0.0;
                
                return new SessionStatistics(
                    totalSessions,
                    activeSessions.size(),
                    abandonedSessions,
                    completedSessions,
                    avgDuration,
                    conversionRate,
                    Instant.now()
                );
            });
    }

    /**
     * Session statistics record.
     */
    public record SessionStatistics(
            long totalSessions,
            int activeSessions,
            long abandonedSessions,
            long completedSessions,
            double averageDurationMinutes,
            double conversionRate,
            Instant calculatedAt
    ) {}
}