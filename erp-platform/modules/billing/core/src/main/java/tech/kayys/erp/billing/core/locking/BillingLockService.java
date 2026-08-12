package tech.kayys.erp.billing.core.locking;

import io.quarkus.redis.client.RedisClient;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

/**
 * Distributed lock service for billing operations.
 * Prevents concurrent processing of the same billing resource.
 */
@ApplicationScoped
public class BillingLockService {

    private static final Logger log = LoggerFactory.getLogger(BillingLockService.class);

    @Inject
    RedisClient redisClient;

    private static final String BILLING_LOCK_PREFIX = "billing:lock:";

    /**
     * Acquires a lock for a billing resource.
     */
    public Uni<Boolean> acquireLock(String resourceId, Duration ttl) {
        String lockKey = BILLING_LOCK_PREFIX + resourceId;
        
        return Uni.createFrom()
            .completionStage(redisClient.setnx(lockKey, "locked"))
            .onItem()
            .transform(result -> {
                if (result != null && "OK".equals(result)) {
                    redisClient.expire(lockKey, (int) ttl.getSeconds());
                    log.debug("Lock acquired for resource: {}", resourceId);
                    return true;
                }
                log.debug("Lock not acquired for resource: {}", resourceId);
                return false;
            });
    }

    /**
     * Releases a lock for a billing resource.
     */
    public Uni<Void> releaseLock(String resourceId) {
        String lockKey = BILLING_LOCK_PREFIX + resourceId;
        return Uni.createFrom()
            .completionStage(redisClient.del(lockKey))
            .onItem()
            .transform(v -> {
                log.debug("Lock released for resource: {}", resourceId);
                return null;
            });
    }

    /**
     * Executes an operation with a lock.
     */
    public <T> Uni<T> withLock(String resourceId, Duration ttl, java.util.function.Supplier<Uni<T>> operation) {
        return acquireLock(resourceId, ttl)
            .onItem()
            .transformToUni(locked -> {
                if (!locked) {
                    return Uni.createFrom().failure(
                        new IllegalStateException("Failed to acquire lock for resource: " + resourceId)
                    );
                }
                return operation.get()
                    .eventually(() -> releaseLock(resourceId));
            });
    }
}