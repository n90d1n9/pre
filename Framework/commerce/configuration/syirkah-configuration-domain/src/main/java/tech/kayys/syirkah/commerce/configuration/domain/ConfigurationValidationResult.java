package tech.kayys.syirkah.commerce.configuration.domain;

import java.util.List;
import java.util.Objects;

/**
 * Outcome of validating a {@link ProductConfiguration} against the
 * product's {@code ProductSpecification} option groups.
 */
public record ConfigurationValidationResult(
        boolean valid,
        List<String> violations
) {

    public ConfigurationValidationResult {
        Objects.requireNonNull(violations, "violations cannot be null");
        violations = List.copyOf(violations);
        if (valid && !violations.isEmpty()) {
            throw new IllegalArgumentException(
                    "A valid result cannot carry violations");
        }
        if (!valid && violations.isEmpty()) {
            throw new IllegalArgumentException(
                    "An invalid result must carry at least one violation");
        }
    }

    public static ConfigurationValidationResult valid() {
        return new ConfigurationValidationResult(true, List.of());
    }

    public static ConfigurationValidationResult invalid(List<String> violations) {
        return new ConfigurationValidationResult(false, violations);
    }

    public static ConfigurationValidationResult invalid(String... violations) {
        return new ConfigurationValidationResult(false, List.of(violations));
    }
}
