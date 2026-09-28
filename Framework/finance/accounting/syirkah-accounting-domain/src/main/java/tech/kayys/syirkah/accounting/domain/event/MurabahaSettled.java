package tech.kayys.syirkah.accounting.domain.event;

import tech.kayys.syirkah.accounting.domain.ledger.LedgerId;
import tech.kayys.syirkah.accounting.domain.multitenancy.TenantId;

import java.time.Instant;
import java.util.UUID;

/**
 * Raised when the customer fully settles a Murabahah contract.
 */
public record MurabahaSettled(
        UUID eventId,
        Instant occurredAt,
        TenantId tenantId,
        LedgerId ledgerId,
        String correlationId,
        String causationId,
        String contractId
) implements AccountingEvent {

    /** Convenience factory. */
    public static MurabahaSettled of(
            TenantId tenantId,
            LedgerId ledgerId,
            String contractId,
            String correlationId,
            String causationId) {
        return new MurabahaSettled(
                UUID.randomUUID(), Instant.now(),
                tenantId, ledgerId, correlationId, causationId, contractId);
    }
}
