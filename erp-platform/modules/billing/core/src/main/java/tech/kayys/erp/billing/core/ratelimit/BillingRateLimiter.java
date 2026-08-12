package tech.kayys.erp.billing.core.ratelimit;

import io.quarkus.redis.client.RedisClient;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;

/**
 * Billing rate limiter.
 * Prevents abuse and ensures fair usage.
 */
@ApplicationScoped
public class BillingRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(BillingRateLimiter.class);

    @Inject
    RedisClient redisClient;

    private static final String RATE_LIMIT_PREFIX = "ratelimit:billing:";

    /**
     * Checks if a request is allowed based on rate limits.
     */
    public Uni<RateLimitResult> checkRateLimit(
            String customerId,
            String operationType,
            int maxRequests,
            Duration timeWindow) {
        
        String key = RATE_LIMIT_PREFIX + customerId + ":" + operationType;
        
        return Uni.createFrom()
            .completionStage(redisClient.incr(key))
            .onItem()
            .transformToUni(currentCount -> {
                if (currentCount == 1) {
                    // First request, set expiry
                    return Uni.createFrom()
                        .completionStage(redisClient.expire(key, (int) timeWindow.getSeconds()))
                        .onItem()
                        .transform(v -> {
                            log.debug("Rate limit initialised for {}: {}", customerId, operationType);
                            return new RateLimitResult(true, currentCount, maxRequests);
                        });
                }

                boolean allowed = currentCount <= maxRequests;
                if (!allowed) {
                    log.warn("Rate limit exceeded for {}: {} ({} requests in window)", 
                        customerId, operationType, currentCount);
                }

                return Uni.createFrom().item(new RateLimitResult(allowed, currentCount, maxRequests));
            })
            .onFailure()
            .recoverWithItem(throwable -> {
                log.error("Rate limit check failed", throwable);
                // Allow on failure to avoid blocking
                return new RateLimitResult(true, 1, maxRequests);
            });
    }

    /**
     * Rate limit result record.
     */
    public record RateLimitResult(
        boolean allowed,
        long currentCount,
        int maxAllowed
    ) {
        public double getUsagePercentage() {
            return (double) currentCount / maxAllowed * 100.0;
        }
    }
}