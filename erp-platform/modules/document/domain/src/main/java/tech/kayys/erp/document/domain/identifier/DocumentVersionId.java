package tech.kayys.erp.document.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Document version identifier.
 */
public final class DocumentVersionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DocumentVersionId(UUID value) {
        super(value);
    }

    public static DocumentVersionId of(UUID value) {
        return new DocumentVersionId(value);
    }

    public static DocumentVersionId generate() {
        return new DocumentVersionId(UUID.randomUUID());
    }

    public static DocumentVersionId fromString(String value) {
        return new DocumentVersionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DocumentVersionId{" + value + "}";
    }
}