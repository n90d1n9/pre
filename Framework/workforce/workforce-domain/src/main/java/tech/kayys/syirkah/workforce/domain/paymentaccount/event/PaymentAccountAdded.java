package tech.kayys.syirkah.workforce.domain.paymentaccount.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountId;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountPurpose;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountType;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PaymentAccountAdded(
        UUID eventId,
        Instant occurredAt,
        PaymentAccountId paymentAccountId,
        WorkerId workerId,
        PaymentAccountType type,
        PaymentAccountPurpose purpose
) implements DomainEvent {
    public PaymentAccountAdded(
            PaymentAccountId paymentAccountId,
            WorkerId workerId,
            PaymentAccountType type,
            PaymentAccountPurpose purpose
    ) {
        this(UUID.randomUUID(), Instant.now(), paymentAccountId, workerId, type, purpose);
    }

    @Override
    public String eventType() { return "workforce.paymentaccount.added"; }
}
