package tech.kayys.syirkah.commerce.configuration.domain;

import java.util.Objects;

/**
 * One customer choice inside an option group (e.g. SIZE=LARGE).
 *
 * Mirrors the blueprint's {@code SelectedOption(groupCode, optionCode)}.
 * Carries NO price — the commercial effect of a selection belongs to
 * the pricing capability ({@code PriceAdjustment}).
 */
public record SelectedOption(
        String groupCode,
        String optionCode
) {

    public SelectedOption {
        groupCode = requireCode(groupCode, "Group code");
        optionCode = requireCode(optionCode, "Option code");
    }

    private static String requireCode(String value, String field) {
        Objects.requireNonNull(value, field + " cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be blank");
        }
        return value.trim();
    }
}
