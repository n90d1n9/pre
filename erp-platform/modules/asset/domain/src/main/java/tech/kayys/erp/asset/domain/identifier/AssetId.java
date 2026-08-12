package tech.kayys.erp.asset.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Asset identifier.
 */
public final class AssetId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AssetId(UUID value) {
        super(value);
    }

    public static AssetId of(UUID value) {
        return new AssetId(value);
    }

    public static AssetId generate() {
        return new AssetId(UUID.randomUUID());
    }

    public static AssetId fromString(String value) {
        return new AssetId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AssetId{" + value + "}";
    }
}