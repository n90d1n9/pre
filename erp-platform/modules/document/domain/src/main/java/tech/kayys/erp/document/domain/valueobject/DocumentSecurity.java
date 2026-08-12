package tech.kayys.erp.document.domain.valueobject;

/**
 * Security classification of documents.
 */
public enum DocumentSecurity {
    PUBLIC("Public - accessible to all"),
    INTERNAL("Internal - accessible within organization"),
    CONFIDENTIAL("Confidential - restricted access"),
    RESTRICTED("Restricted - very limited access"),
    TOP_SECRET("Top Secret - highest classification");

    private final String description;

    DocumentSecurity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getLevel() {
        return switch (this) {
            case PUBLIC -> 1;
            case INTERNAL -> 2;
            case CONFIDENTIAL -> 3;
            case RESTRICTED -> 4;
            case TOP_SECRET -> 5;
        };
    }

    public boolean hasAccess(DocumentSecurity userLevel) {
        return userLevel.getLevel() >= this.getLevel();
    }
}