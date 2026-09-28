package tech.kayys.syirkah.reliability.spi.port;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.reliability.idempotency.IdempotencyOutcome;

/**
 * Claims idempotency keys for write operations (base01.md §P1-11, §P1-15).
 *
 * <p>Both event consumption and caller-facing writes use this, so "the same
 * request submitted twice" behaves identically in both paths.
 */
public interface IdempotencyStorePort {

    Uni<IdempotencyOutcome> claim(String key, String scope);

    Uni<Void> recordResult(String key, String scope, String resultRef);
}
