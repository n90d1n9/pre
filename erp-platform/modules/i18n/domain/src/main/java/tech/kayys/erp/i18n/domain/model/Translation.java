package tech.kayys.erp.i18n.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.i18n.domain.identifier.TranslationId;

import java.time.Instant;

/**
 * Translation aggregate root.
 * Manages multi-language translations across all modules.
 */
public final class Translation extends AggregateRoot<TranslationId> {
    
    private static final long serialVersionUID = 1L;
    
    private String key;
    private String module;
    private String context;
    private String locale;
    private String translation;
    private String fallbackTranslation;
    private String notes;
    private boolean active;

    private Translation(TranslationId id) {
        super(id);
        this.active = true;
    }

    private Translation() {
        super();
    }

    /**
     * Factory method to create a new translation.
     */
    public static Translation create(
            TranslationId id,
            String key,
            String module,
            String context,
            String locale,
            String translation) {
        Translation trans = new Translation(id);
        trans.key = key;
        trans.module = module;
        trans.context = context;
        trans.locale = locale;
        trans.translation = translation;
        return trans;
    }

    /**
     * Updates the translation.
     */
    public void updateTranslation(String translation) {
        this.translation = translation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the fallback translation.
     */
    public void setFallbackTranslation(String fallbackTranslation) {
        this.fallbackTranslation = fallbackTranslation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the translation.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the translation.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getKey() { return key; }
    public String getModule() { return module; }
    public String getContext() { return context; }
    public String getLocale() { return locale; }
    public String getTranslation() { return translation; }
    public String getFallbackTranslation() { return fallbackTranslation; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Translation{" +
                "id=" + getId() +
                ", key='" + key + '\'' +
                ", module='" + module + '\'' +
                ", locale='" + locale + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}