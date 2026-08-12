package tech.kayys.erp.document.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Document identifier.
 */
public final class DocumentId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DocumentId(UUID value) {
        super(value);
    }

    public static DocumentId of(UUID value) {
        return new DocumentId(value);
    }

    public static DocumentId generate() {
        return new DocumentId(UUID.randomUUID());
    }

    public static DocumentId fromString(String value) {
        return new DocumentId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DocumentId{" + value + "}";
    }
}