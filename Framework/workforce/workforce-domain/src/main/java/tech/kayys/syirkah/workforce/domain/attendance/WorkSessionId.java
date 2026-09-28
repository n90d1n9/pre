package tech.kayys.syirkah.workforce.domain.attendance;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;
import java.util.UUID;

public record WorkSessionId(UUID value) implements DomainId<UUID> {
    public static WorkSessionId of(UUID value) { return new WorkSessionId(value); }
    public static WorkSessionId generate() { return new WorkSessionId(UUID.randomUUID()); }
}
