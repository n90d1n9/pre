package tech.kayys.erp.omnichannel.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Channel identifier.
 */
public final class ChannelId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ChannelId(UUID value) {
        super(value);
    }

    public static ChannelId of(UUID value) {
        return new ChannelId(value);
    }

    public static ChannelId generate() {
        return new ChannelId(UUID.randomUUID());
    }

    public static ChannelId fromString(String value) {
        return new ChannelId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ChannelId{" + value + "}";
    }
}