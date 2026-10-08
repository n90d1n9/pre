package tech.kayys.syirkah.workforce.domain.paymentaccount.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record PaymentAccountDeactivated(
        UUID eventId,
        Instant occurredAt,
        PaymentAccountId paymentAccountId,
        WorkerId workerId
) implements DomainEvent {
    public PaymentAccountDeactivated(PaymentAccountId paymentAccountId, WorkerId workerId) {
        this(UUID.randomUUID(), Instant.now(), paymentAccountId, workerId);
    }

    @Override
    public String eventType() { return "workforce.paymentaccount.deactivated"; }
}
