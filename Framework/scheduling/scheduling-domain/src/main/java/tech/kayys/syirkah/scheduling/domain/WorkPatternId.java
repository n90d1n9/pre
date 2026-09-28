package tech.kayys.syirkah.scheduling.domain;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;
import java.util.UUID;

public record WorkPatternId(UUID value) implements DomainId<UUID> {
    public static WorkPatternId of(UUID v) { return new WorkPatternId(v); }
    public static WorkPatternId generate() { return new WorkPatternId(UUID.randomUUID()); }
}
