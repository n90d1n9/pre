package tech.kayys.syirkah.accounting.domain.event;

import tech.kayys.syirkah.accounting.domain.identifier.JournalEntryId;
import tech.kayys.syirkah.accounting.domain.ledger.LedgerId;
import tech.kayys.syirkah.accounting.domain.multitenancy.TenantId;

import java.time.Instant;
import java.util.UUID;

public record JournalEntryPosted(
        UUID eventId,
        Instant occurredAt,
        TenantId tenantId,
        LedgerId ledgerId,
        JournalEntryId journalEntryId,
        String entryNumber,
        String postedBy,
        String correlationId,
        String causationId
) implements AccountingEvent {
    public static JournalEntryPosted of(TenantId tenantId, LedgerId ledgerId, JournalEntryId id, String entryNumber, String postedBy, String corrId, String causId) {
        return new JournalEntryPosted(UUID.randomUUID(), Instant.now(), tenantId, ledgerId, id, entryNumber, postedBy, corrId, causId);
    }
}
