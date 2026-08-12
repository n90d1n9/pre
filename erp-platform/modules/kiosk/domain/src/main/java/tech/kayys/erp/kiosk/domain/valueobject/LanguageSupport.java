package tech.kayys.erp.kiosk.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Language support configuration for the kiosk.
 */
public final class LanguageSupport implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final List<Language> supportedLanguages;
    private final String defaultLanguage;
    private final boolean autoDetect;

    public LanguageSupport(List<Language> supportedLanguages, String defaultLanguage, boolean autoDetect) {
        this.supportedLanguages = new ArrayList<>(supportedLanguages);
        this.defaultLanguage = defaultLanguage;
        this.autoDetect = autoDetect;
        validate();
    }

    @Override
    public void validate() {
        if (supportedLanguages.isEmpty()) {
            throw new IllegalArgumentException("At least one language must be supported");
        }
        if (defaultLanguage == null || defaultLanguage.trim().isEmpty()) {
            throw new IllegalArgumentException("Default language cannot be empty");
        }
        // Check if default language is in the supported list
        boolean found = supportedLanguages.stream()
            .anyMatch(lang -> lang.getCode().equals(defaultLanguage));
        if (!found) {
            throw new IllegalArgumentException("Default language must be in supported languages");
        }
    }

    public List<Language> getSupportedLanguages() { return Collections.unmodifiableList(supportedLanguages); }
    public String getDefaultLanguage() { return defaultLanguage; }
    public boolean isAutoDetect() { return autoDetect; }

    public Language getLanguage(String code) {
        return supportedLanguages.stream()
            .filter(lang -> lang.getCode().equals(code))
            .findFirst()
            .orElse(null);
    }

    public boolean supportsLanguage(String code) {
        return supportedLanguages.stream().anyMatch(lang -> lang.getCode().equals(code));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageSupport that = (LanguageSupport) o;
        return Objects.equals(defaultLanguage, that.defaultLanguage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultLanguage);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Language> supportedLanguages = new ArrayList<>();
        private String defaultLanguage = "en";
        private boolean autoDetect = true;

        public Builder addLanguage(Language language) {
            this.supportedLanguages.add(language);
            return this;
        }

        public Builder defaultLanguage(String defaultLanguage) {
            this.defaultLanguage = defaultLanguage;
            return this;
        }

        public Builder autoDetect(boolean autoDetect) {
            this.autoDetect = autoDetect;
            return this;
        }

        public LanguageSupport build() {
            if (supportedLanguages.isEmpty()) {
                supportedLanguages.add(Language.ENGLISH);
            }
            return new LanguageSupport(supportedLanguages, defaultLanguage, autoDetect);
        }
    }

    /**
     * Language value object.
     */
    public static final class Language implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String code;
        private final String name;
        private final String nativeName;
        private final String direction; // LTR or RTL
        private final String flagEmoji;

        public Language(String code, String name, String nativeName, String direction, String flagEmoji) {
            this.code = code;
            this.name = name;
            this.nativeName = nativeName;
            this.direction = direction;
            this.flagEmoji = flagEmoji;
            validate();
        }

        @Override
        public void validate() {
            if (code == null || code.trim().isEmpty()) {
                throw new IllegalArgumentException("Language code cannot be empty");
            }
            if (!direction.equals("LTR") && !direction.equals("RTL")) {
                throw new IllegalArgumentException("Direction must be LTR or RTL");
            }
        }

        public String getCode() { return code; }
        public String getName() { return name; }
        public String getNativeName() { return nativeName; }
        public String getDirection() { return direction; }
        public String getFlagEmoji() { return flagEmoji; }

        public boolean isRTL() { return "RTL".equals(direction); }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Language language = (Language) o;
            return Objects.equals(code, language.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code);
        }

        @Override
        public String toString() {
            return "Language{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }

        // Common Languages
        public static final Language ENGLISH = new Language("en", "English", "English", "LTR", "🇬🇧");
        public static final Language SPANISH = new Language("es", "Spanish", "Español", "LTR", "🇪🇸");
        public static final Language FRENCH = new Language("fr", "French", "Français", "LTR", "🇫🇷");
        public static final Language GERMAN = new Language("de", "German", "Deutsch", "LTR", "🇩🇪");
        public static final Language CHINESE = new Language("zh", "Chinese", "中文", "LTR", "🇨🇳");
        public static final Language JAPANESE = new Language("ja", "Japanese", "日本語", "LTR", "🇯🇵");
        public static final Language ARABIC = new Language("ar", "Arabic", "العربية", "RTL", "🇸🇦");
        public static final Language PORTUGUESE = new Language("pt", "Portuguese", "Português", "LTR", "🇵🇹");
        public static final Language ITALIAN = new Language("it", "Italian", "Italiano", "LTR", "🇮🇹");
        public static final Language KOREAN = new Language("ko", "Korean", "한국어", "LTR", "🇰🇷");
    }
}