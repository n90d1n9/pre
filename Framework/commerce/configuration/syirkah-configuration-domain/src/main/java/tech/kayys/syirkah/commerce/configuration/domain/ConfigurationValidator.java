package tech.kayys.syirkah.commerce.configuration.domain;

import tech.kayys.syirkah.product.domain.specification.ProductSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Pure-domain validator: checks a {@link ProductConfiguration} against
 * the option groups of a {@link ProductSpecification}.
 *
 * Rules (from product01.md §4 + §6):
 * <ul>
 *   <li>every required group must have a selection;</li>
 *   <li>every selection must reference a known group;</li>
 *   <li>every selected option must exist inside its group.</li>
 * </ul>
 * Unknown groups, unknown options and missing required groups are
 * reported as violations — never silently ignored.
 */
public final class ConfigurationValidator {

    private ConfigurationValidator() {
    }

    public static ConfigurationValidationResult validate(
            ProductSpecification specification,
            ProductConfiguration configuration
    ) {
        Objects.requireNonNull(specification, "specification cannot be null");
        Objects.requireNonNull(configuration, "configuration cannot be null");

        if (!specification.productId().equals(configuration.productId())) {
            return ConfigurationValidationResult.invalid(
                    "Configuration is for product "
                            + configuration.productId().value()
                            + " but specification belongs to product "
                            + specification.productId().value());
        }

        List<String> violations = new ArrayList<>();
        var groupsByCode = new java.util.HashMap<String,
                tech.kayys.syirkah.product.domain.specification.OptionGroup>();
        for (var group : specification.optionGroups()) {
            groupsByCode.put(group.code(), group);
        }

        for (var group : specification.optionGroups()) {
            var selection = configuration.options().get(group.code());
            if (selection == null && group.required()) {
                violations.add(
                        "Missing required selection for group: " + group.code());
            }
        }

        for (var selection : configuration.options().values()) {
            var group = groupsByCode.get(selection.groupCode());
            if (group == null) {
                violations.add(
                        "Unknown option group: " + selection.groupCode());
                continue;
            }
            boolean known = group.options().stream()
                    .anyMatch(option -> option.code().equals(selection.optionCode()));
            if (!known) {
                violations.add(
                        "Unknown option '" + selection.optionCode()
                                + "' in group '" + selection.groupCode() + "'");
            }
        }

        if (violations.isEmpty()) {
            return ConfigurationValidationResult.valid();
        }
        return ConfigurationValidationResult.invalid(violations);
    }
}
