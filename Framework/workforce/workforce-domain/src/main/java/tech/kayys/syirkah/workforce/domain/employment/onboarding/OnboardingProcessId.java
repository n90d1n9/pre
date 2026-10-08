package tech.kayys.syirkah.workforce.domain.employment.onboarding;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;

import java.util.Objects;
import java.util.UUID;

public record OnboardingProcessId(UUID value) implements DomainId<UUID> {
    public OnboardingProcessId {
        Objects.requireNonNull(value, "value must not be null");
    }

    public static OnboardingProcessId generate() {
        return new OnboardingProcessId(UUID.randomUUID());
    }

    public static OnboardingProcessId of(UUID value) {
        return new OnboardingProcessId(value);
    }
}
