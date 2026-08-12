package tech.kayys.erp.billing.core.idempotency;

import io.quarkus.redis.client.RedisClient;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.CompletionStage;

/**
 * Idempotency service using Redis for distributed locking.
 * Ensures that operations are processed exactly once.
 */
@ApplicationScoped
public class IdempotencyService {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyService.class);

    @Inject
    RedisClient redisClient;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final String LOCK_PREFIX = "lock:";

    /**
     * Executes an idempotent operation.
     * Returns cached result if already processed.
     */
    public <T> Uni<T> executeIdempotent(
            IdempotencyKey key,
            java.util.function.Supplier<Uni<T>> operation,
            java.util.function.Function<T, String> resultSerializer,
            java.util.function.Function<String, T> resultDeserializer) {
        
        String redisKey = IDEMPOTENCY_PREFIX + key.getKey();
        String lockKey = LOCK_PREFIX + key.getKey();

        // Check if already processed
        return Uni.createFrom()
            .completionStage(redisClient.get(redisKey))
            .onItem()
            .transformToUni(cachedResult -> {
                if (cachedResult != null) {
                    log.debug("Idempotent operation already processed: {}", key);
                    return Uni.createFrom().item(resultDeserializer.apply(cachedResult));
                }

                // Acquire distributed lock
                return acquireLock(lockKey, key.getTtlSeconds())
                    .onItem()
                    .transformToUni(locked -> {
                        if (!locked) {
                            // Wait and retry if lock not acquired
                            log.warn("Failed to acquire lock for idempotent operation: {}", key);
                            return Uni.createFrom().failure(
                                new IllegalStateException("Failed to acquire lock for idempotent operation")
                            );
                        }

                        // Execute operation
                        return operation.get()
                            .onItem()
                            .transformToUni(result -> {
                                // Cache result
                                String serialized = resultSerializer.apply(result);
                                return Uni.createFrom()
                                    .completionStage(redisClient.setex(redisKey, key.getTtlSeconds(), serialized))
                                    .onItem()
                                    .transform(v -> {
                                        releaseLock(lockKey);
                                        return result;
                                    });
                            })
                            .onFailure()
                            .recoverWithUni(throwable -> {
                                releaseLock(lockKey);
                                return Uni.createFrom().failure(throwable);
                            });
                    });
            });
    }

    /**
     * Acquires a distributed lock.
     */
    private Uni<Boolean> acquireLock(String lockKey, int ttlSeconds) {
        return Uni.createFrom()
            .completionStage(redisClient.setnx(lockKey, "locked"))
            .onItem()
            .transform(result -> {
                if (result != null && "OK".equals(result)) {
                    redisClient.expire(lockKey, ttlSeconds);
                    return true;
                }
                return false;
            })
            .onFailure()
            .recoverWithItem(false);
    }

    /**
     * Releases a distributed lock.
     */
    private void releaseLock(String lockKey) {
        try {
            redisClient.del(lockKey);
        } catch (Exception e) {
            log.warn("Failed to release lock: {}", lockKey, e);
        }
    }

    /**
     * Checks if an operation is already processed.
     */
    public Uni<Boolean> isProcessed(IdempotencyKey key) {
        String redisKey = IDEMPOTENCY_PREFIX + key.getKey();
        return Uni.createFrom()
            .completionStage(redisClient.exists(redisKey))
            .onItem()
            .transform(count -> count > 0);
    }

    /**
     * Invalidates an idempotency key.
     */
    public Uni<Void> invalidate(IdempotencyKey key) {
        String redisKey = IDEMPOTENCY_PREFIX + key.getKey();
        return Uni.createFrom()
            .completionStage(redisClient.del(redisKey))
            .onItem()
            .transform(v -> null);
    }
}