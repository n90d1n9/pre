package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class KnowledgeArticleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public KnowledgeArticleId(UUID value) {
        super(value);
    }

    public static KnowledgeArticleId of(UUID value) {
        return new KnowledgeArticleId(value);
    }

    public static KnowledgeArticleId generate() {
        return new KnowledgeArticleId(UUID.randomUUID());
    }

    public static KnowledgeArticleId fromString(String value) {
        return new KnowledgeArticleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "KnowledgeArticleId{" + value + "}";
    }
}
