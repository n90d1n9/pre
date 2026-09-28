package tech.kayys.syirkah.workforce.domain.payroll;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record PayrollPeriodId(UUID value) implements DomainId<UUID> {
    public PayrollPeriodId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static PayrollPeriodId of(UUID value) { return new PayrollPeriodId(value); }
    public static PayrollPeriodId of(String value) { return new PayrollPeriodId(UUID.fromString(value)); }
    public static PayrollPeriodId generate() { return new PayrollPeriodId(UUID.randomUUID()); }

    @Override
    public String toString() { return value.toString(); }
}
