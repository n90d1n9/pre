package tech.kayys.syirkah.workforce.domain.benefit;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record BenefitId(UUID value) implements DomainId<UUID> {
    public BenefitId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static BenefitId of(UUID value) { return new BenefitId(value); }
    public static BenefitId of(String value) { return new BenefitId(UUID.fromString(value)); }
    public static BenefitId generate() { return new BenefitId(UUID.randomUUID()); }

    @Override
    public String toString() { return value.toString(); }
}
