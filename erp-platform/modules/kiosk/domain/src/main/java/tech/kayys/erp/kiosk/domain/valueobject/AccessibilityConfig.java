package tech.kayys.erp.kiosk.domain.valueobject;

import tech.kayys.erp.foundation.domain.ValueObject;

/**
 * Accessibility configuration for the kiosk.
 */
public final class AccessibilityConfig implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final boolean screenReaderEnabled;
    private final boolean highContrastMode;
    private final boolean largeTextMode;
    private final boolean audioFeedbackEnabled;
    private final boolean tactileFeedbackEnabled;
    private final boolean wheelChairAccessible;
    private final int fontSizeScale;
    private final double contrastRatio;
    private final boolean voiceCommandsEnabled;

    public AccessibilityConfig(
            boolean screenReaderEnabled,
            boolean highContrastMode,
            boolean largeTextMode,
            boolean audioFeedbackEnabled,
            boolean tactileFeedbackEnabled,
            boolean wheelChairAccessible,
            int fontSizeScale,
            double contrastRatio,
            boolean voiceCommandsEnabled) {
        this.screenReaderEnabled = screenReaderEnabled;
        this.highContrastMode = highContrastMode;
        this.largeTextMode = largeTextMode;
        this.audioFeedbackEnabled = audioFeedbackEnabled;
        this.tactileFeedbackEnabled = tactileFeedbackEnabled;
        this.wheelChairAccessible = wheelChairAccessible;
        this.fontSizeScale = fontSizeScale;
        this.contrastRatio = contrastRatio;
        this.voiceCommandsEnabled = voiceCommandsEnabled;
        validate();
    }

    @Override
    public void validate() {
        if (fontSizeScale < 1 || fontSizeScale > 300) {
            throw new IllegalArgumentException("Font size scale must be between 1 and 300");
        }
        if (contrastRatio < 1.0 || contrastRatio > 21.0) {
            throw new IllegalArgumentException("Contrast ratio must be between 1.0 and 21.0");
        }
    }

    // Getters
    public boolean isScreenReaderEnabled() { return screenReaderEnabled; }
    public boolean isHighContrastMode() { return highContrastMode; }
    public boolean isLargeTextMode() { return largeTextMode; }
    public boolean isAudioFeedbackEnabled() { return audioFeedbackEnabled; }
    public boolean isTactileFeedbackEnabled() { return tactileFeedbackEnabled; }
    public boolean isWheelChairAccessible() { return wheelChairAccessible; }
    public int getFontSizeScale() { return fontSizeScale; }
    public double getContrastRatio() { return contrastRatio; }
    public boolean isVoiceCommandsEnabled() { return voiceCommandsEnabled; }

    public static AccessibilityConfig defaultConfig() {
        return new AccessibilityConfig(
            false, // screenReaderEnabled
            false, // highContrastMode
            false, // largeTextMode
            true,  // audioFeedbackEnabled
            true,  // tactileFeedbackEnabled
            true,  // wheelChairAccessible
            100,   // fontSizeScale
            4.5,   // contrastRatio
            false  // voiceCommandsEnabled
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean screenReaderEnabled = false;
        private boolean highContrastMode = false;
        private boolean largeTextMode = false;
        private boolean audioFeedbackEnabled = true;
        private boolean tactileFeedbackEnabled = true;
        private boolean wheelChairAccessible = true;
        private int fontSizeScale = 100;
        private double contrastRatio = 4.5;
        private boolean voiceCommandsEnabled = false;

        public Builder screenReaderEnabled(boolean screenReaderEnabled) {
            this.screenReaderEnabled = screenReaderEnabled;
            return this;
        }

        public Builder highContrastMode(boolean highContrastMode) {
            this.highContrastMode = highContrastMode;
            return this;
        }

        public Builder largeTextMode(boolean largeTextMode) {
            this.largeTextMode = largeTextMode;
            return this;
        }

        public Builder audioFeedbackEnabled(boolean audioFeedbackEnabled) {
            this.audioFeedbackEnabled = audioFeedbackEnabled;
            return this;
        }

        public Builder tactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
            this.tactileFeedbackEnabled = tactileFeedbackEnabled;
            return this;
        }

        public Builder wheelChairAccessible(boolean wheelChairAccessible) {
            this.wheelChairAccessible = wheelChairAccessible;
            return this;
        }

        public Builder fontSizeScale(int fontSizeScale) {
            this.fontSizeScale = fontSizeScale;
            return this;
        }

        public Builder contrastRatio(double contrastRatio) {
            this.contrastRatio = contrastRatio;
            return this;
        }

        public Builder voiceCommandsEnabled(boolean voiceCommandsEnabled) {
            this.voiceCommandsEnabled = voiceCommandsEnabled;
            return this;
        }

        public AccessibilityConfig build() {
            return new AccessibilityConfig(
                screenReaderEnabled, highContrastMode, largeTextMode,
                audioFeedbackEnabled, tactileFeedbackEnabled,
                wheelChairAccessible, fontSizeScale, contrastRatio,
                voiceCommandsEnabled
            );
        }
    }
}