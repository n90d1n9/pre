package tech.kayys.syirkah.accounting.domain.event;

import tech.kayys.syirkah.accounting.domain.identifier.FiscalPeriodId;
import tech.kayys.syirkah.accounting.domain.ledger.LedgerId;
import tech.kayys.syirkah.accounting.domain.multitenancy.TenantId;

import java.time.Instant;
import java.util.UUID;

public record FiscalPeriodClosed(
        UUID eventId,
        Instant occurredAt,
        TenantId tenantId,
        LedgerId ledgerId,
        FiscalPeriodId periodId,
        String periodName,
        String closedBy,
        String correlationId,
        String causationId
) implements AccountingEvent {
    public static FiscalPeriodClosed of(TenantId tenantId, LedgerId ledgerId, FiscalPeriodId id, String name, String closedBy, String corrId, String causId) {
        return new FiscalPeriodClosed(UUID.randomUUID(), Instant.now(), tenantId, ledgerId, id, name, closedBy, corrId, causId);
    }
}
