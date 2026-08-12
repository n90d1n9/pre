package tech.kayys.erp.hris.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Onboarding process identifier.
 */
public final class OnboardingId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OnboardingId(UUID value) {
        super(value);
    }

    public static OnboardingId of(UUID value) {
        return new OnboardingId(value);
    }

    public static OnboardingId generate() {
        return new OnboardingId(UUID.randomUUID());
    }

    public static OnboardingId fromString(String value) {
        return new OnboardingId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OnboardingId{" + value + "}";
    }
}