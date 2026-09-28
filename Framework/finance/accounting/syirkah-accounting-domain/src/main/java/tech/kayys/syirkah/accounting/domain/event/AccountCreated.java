package tech.kayys.syirkah.accounting.domain.event;

import tech.kayys.syirkah.accounting.domain.identifier.AccountId;
import tech.kayys.syirkah.accounting.domain.ledger.LedgerId;
import tech.kayys.syirkah.accounting.domain.multitenancy.TenantId;
import tech.kayys.syirkah.accounting.domain.valueobject.AccountType;

import java.time.Instant;
import java.util.UUID;

public record AccountCreated(
        UUID eventId,
        Instant occurredAt,
        TenantId tenantId,
        LedgerId ledgerId,
        AccountId accountId,
        String accountNumber,
        String accountName,
        AccountType accountType,
        String correlationId,
        String causationId
) implements AccountingEvent {
    public static AccountCreated of(TenantId tenantId, LedgerId ledgerId, AccountId id, String num, String name, AccountType type, String corrId, String causId) {
        return new AccountCreated(UUID.randomUUID(), Instant.now(), tenantId, ledgerId, id, num, name, type, corrId, causId);
    }
}
