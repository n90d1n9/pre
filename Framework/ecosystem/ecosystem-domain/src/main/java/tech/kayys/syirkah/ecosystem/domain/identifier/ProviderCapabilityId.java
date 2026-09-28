package tech.kayys.syirkah.ecosystem.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifies a provider capability - the claim "this participant can
 * supply this capability, under these terms".
 *
 * <p>Separating {@code Provider} from {@code Capability} is what lets one
 * participant offer logistics while another offers warehousing, and lets
 * the same capability be offered by an internal team, a Syirkah service,
 * or an external 3PL - see {@code base00.md} §1 and §2.
 */
public final class ProviderCapabilityId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public ProviderCapabilityId(UUID value) {
        super(value);
    }

    public static ProviderCapabilityId of(UUID value) {
        return new ProviderCapabilityId(value);
    }

    public static ProviderCapabilityId generate() {
        return new ProviderCapabilityId(UUID.randomUUID());
    }

    public static ProviderCapabilityId fromString(String value) {
        return new ProviderCapabilityId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ProviderCapabilityId{" + value + "}";
    }
}
