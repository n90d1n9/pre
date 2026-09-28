package tech.kayys.syirkah.ecosystem.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifies a participant relationship - the directed link that lets
 * Syirkah understand "PT XYZ buys from PT ABC" or "PT ABC serves
 * 200 merchants" without hard-coding either side.
 */
public final class RelationshipId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public RelationshipId(UUID value) {
        super(value);
    }

    public static RelationshipId of(UUID value) {
        return new RelationshipId(value);
    }

    public static RelationshipId generate() {
        return new RelationshipId(UUID.randomUUID());
    }

    public static RelationshipId fromString(String value) {
        return new RelationshipId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "RelationshipId{" + value + "}";
    }
}
