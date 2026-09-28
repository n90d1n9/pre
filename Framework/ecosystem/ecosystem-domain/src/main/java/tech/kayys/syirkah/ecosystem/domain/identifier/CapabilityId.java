package tech.kayys.syirkah.ecosystem.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Identifies a capability in the capability catalog - for example
 * {@code logistics.transportation}, {@code commerce.retail.pos},
 * {@code finance.accounting}.
 *
 * <p>Capabilities are the platform's extension seam: a participant can
 * consume one without consuming all of Syirkah, and can provide one
 * without running Syirkah software. See {@code base00.md} §14.
 */
public final class CapabilityId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public CapabilityId(UUID value) {
        super(value);
    }

    public static CapabilityId of(UUID value) {
        return new CapabilityId(value);
    }

    public static CapabilityId generate() {
        return new CapabilityId(UUID.randomUUID());
    }

    public static CapabilityId fromString(String value) {
        return new CapabilityId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CapabilityId{" + value + "}";
    }
}
