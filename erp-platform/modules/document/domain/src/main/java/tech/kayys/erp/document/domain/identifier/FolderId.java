package tech.kayys.erp.document.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Folder identifier.
 */
public final class FolderId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public FolderId(UUID value) {
        super(value);
    }

    public static FolderId of(UUID value) {
        return new FolderId(value);
    }

    public static FolderId generate() {
        return new FolderId(UUID.randomUUID());
    }

    public static FolderId fromString(String value) {
        return new FolderId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "FolderId{" + value + "}";
    }
}