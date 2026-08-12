package tech.kayys.erp.asset.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Asset category identifier.
 */
public final class AssetCategoryId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AssetCategoryId(UUID value) {
        super(value);
    }

    public static AssetCategoryId of(UUID value) {
        return new AssetCategoryId(value);
    }

    public static AssetCategoryId generate() {
        return new AssetCategoryId(UUID.randomUUID());
    }

    public static AssetCategoryId fromString(String value) {
        return new AssetCategoryId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AssetCategoryId{" + value + "}";
    }
}