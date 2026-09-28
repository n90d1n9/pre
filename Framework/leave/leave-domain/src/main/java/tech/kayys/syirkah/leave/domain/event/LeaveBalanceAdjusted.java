package tech.kayys.syirkah.leave.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.leave.domain.LeaveBalanceId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LeaveBalanceAdjusted(
        UUID eventId,
        Instant occurredAt,
        LeaveBalanceId balanceId,
        BigDecimal amount,
        String reason
) implements DomainEvent {
    public LeaveBalanceAdjusted(LeaveBalanceId balanceId, BigDecimal amount, String reason) {
        this(UUID.randomUUID(), Instant.now(), balanceId, amount, reason);
    }
    @Override
    public String eventType() {
        return "leave.balance.adjusted";
    }
}
