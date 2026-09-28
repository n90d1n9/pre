package tech.kayys.syirkah.ecosystem.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifies a participant in the Syirkah ecosystem.
 *
 * <p>A participant is an entity that takes part in the ecosystem - it may
 * be a tenant's own operation, a Syirkah-hosted capability, or an external
 * provider. See {@code Docs/Plan/base00.md} §3.
 */
public final class ParticipantId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public ParticipantId(UUID value) {
        super(value);
    }

    public static ParticipantId of(UUID value) {
        return new ParticipantId(value);
    }

    public static ParticipantId generate() {
        return new ParticipantId(UUID.randomUUID());
    }

    public static ParticipantId fromString(String value) {
        return new ParticipantId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ParticipantId{" + value + "}";
    }
}
