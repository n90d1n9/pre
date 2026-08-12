package tech.kayys.erp.billing.core.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Billing health checks for liveness and readiness probes.
 */
@ApplicationScoped
@Liveness
@Readiness
public class BillingHealthCheck implements HealthCheck {

    @Inject
    BillingMetrics metrics;

    @Inject
    BillingLockService lockService;

    @Inject
    IdempotencyService idempotencyService;

    private Instant lastSuccessfulRun;
    private long totalErrors = 0;

    @Override
    public HealthCheckResponse call() {
        Map<String, Object> details = new HashMap<>();
        boolean healthy = true;

        // Check last successful run
        if (lastSuccessfulRun != null) {
            long minutesSinceLastRun = Duration.between(lastSuccessfulRun, Instant.now()).toMinutes();
            if (minutesSinceLastRun > 30) {
                healthy = false;
                details.put("warning", "No successful billing run in " + minutesSinceLastRun + " minutes");
            }
        }

        // Check error rate
        if (totalErrors > 10) {
            healthy = false;
            details.put("errors", totalErrors);
        }

        // Check dependencies
        boolean redisHealthy = checkRedisHealth();
        if (!redisHealthy) {
            healthy = false;
            details.put("redis", "unhealthy");
        }

        return HealthCheckResponse.builder()
            .name("billing")
            .status(healthy)
            .withData("healthy", healthy)
            .withData("lastSuccessfulRun", lastSuccessfulRun != null ? lastSuccessfulRun.toString() : "never")
            .withData("totalErrors", totalErrors)
            .build();
    }

    private boolean checkRedisHealth() {
        try {
            // Test Redis connectivity
            String testKey = "health:test";
            redisClient.set(testKey, "ok");
            String result = redisClient.get(testKey);
            redisClient.del(testKey);
            return "ok".equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    public void recordSuccess() {
        this.lastSuccessfulRun = Instant.now();
    }

    public void recordError() {
        this.totalErrors++;
    }
}