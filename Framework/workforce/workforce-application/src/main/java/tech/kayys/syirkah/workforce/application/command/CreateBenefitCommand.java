package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.benefit.BenefitType;

import java.util.Objects;

public record CreateBenefitCommand(
        TenantId tenantId,
        String code,
        String name,
        BenefitType type
) implements Command {
    public CreateBenefitCommand {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(code, "code must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
