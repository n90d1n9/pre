package tech.kayys.syirkah.support.application.api;

import tech.kayys.syirkah.ecosystem.domain.identifier.ParticipantId;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.identity.domain.user.UserId;

import java.util.Objects;

public record ActorContext(
        TenantId tenantId,
        ActorType type,
        UserId userId,
        ParticipantId participantId
) {
    public ActorContext {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        if (type != ActorType.SYSTEM && userId == null && participantId == null) {
            throw new IllegalArgumentException("A non-system actor must have a user or participant identity");
        }
    }
}
