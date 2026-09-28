package tech.kayys.syirkah.leave.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.leave.domain.LeaveBalanceId;
import tech.kayys.syirkah.leave.domain.LeaveTypeId;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LeaveBalanceAllocated(
        UUID eventId,
        Instant occurredAt,
        LeaveBalanceId balanceId,
        UUID subjectId,
        UUID contextId,
        LeaveTypeId leaveTypeId,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal entitlement
) implements DomainEvent {
    public LeaveBalanceAllocated(LeaveBalanceId balanceId, UUID subjectId, UUID contextId,
                                 LeaveTypeId leaveTypeId, LocalDate periodStart,
                                 LocalDate periodEnd, BigDecimal entitlement) {
        this(UUID.randomUUID(), Instant.now(), balanceId, subjectId, contextId, leaveTypeId, periodStart, periodEnd, entitlement);
    }
    @Override
    public String eventType() {
        return "leave.balance.allocated";
    }
}
