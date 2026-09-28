package tech.kayys.syirkah.leave.domain;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;
import java.util.UUID;

public record LeaveBalanceId(UUID value) implements DomainId<UUID> {
    public static LeaveBalanceId of(UUID v) { return new LeaveBalanceId(v); }
    public static LeaveBalanceId generate() { return new LeaveBalanceId(UUID.randomUUID()); }
}
