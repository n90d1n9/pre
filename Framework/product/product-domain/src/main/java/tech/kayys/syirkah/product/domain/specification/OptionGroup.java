package tech.kayys.syirkah.product.domain.specification;

import java.util.List;
import java.util.Objects;

/**
 * A named group of selectable options (e.g. SIZE with
 * SMALL/MEDIUM/LARGE). Whether selecting one is mandatory is a
 * property of the group, not of Product.
 */
public record OptionGroup(
        String code,
        String name,
        boolean required,
        List<OptionDefinition> options
) {

    public OptionGroup {
        Objects.requireNonNull(options, "options cannot be null");

        code = requireText(code, "Option group code");
        name = requireText(name, "Option group name");

        if (options.isEmpty()) {
            throw new IllegalArgumentException(
                    "Option group must contain at least one option"
            );
        }

        options = List.copyOf(options);
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    field + " cannot be blank"
            );
        }

        return value.trim();
    }
}