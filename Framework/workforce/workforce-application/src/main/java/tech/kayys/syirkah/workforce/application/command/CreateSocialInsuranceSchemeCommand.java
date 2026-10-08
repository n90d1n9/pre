package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;

import java.util.Objects;

public record CreateSocialInsuranceSchemeCommand(
        TenantId tenantId,
        String code,
        String name
) implements Command {
    public CreateSocialInsuranceSchemeCommand {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(name, "name must not be null");
    }
}
