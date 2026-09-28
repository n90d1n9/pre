package tech.kayys.syirkah.ecosystem.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifies an ecosystem contract - the agreed commercial and
 * operational terms under which a provider supplies a capability.
 */
public final class EcosystemContractId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public EcosystemContractId(UUID value) {
        super(value);
    }

    public static EcosystemContractId of(UUID value) {
        return new EcosystemContractId(value);
    }

    public static EcosystemContractId generate() {
        return new EcosystemContractId(UUID.randomUUID());
    }

    public static EcosystemContractId fromString(String value) {
        return new EcosystemContractId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "EcosystemContractId{" + value + "}";
    }
}
