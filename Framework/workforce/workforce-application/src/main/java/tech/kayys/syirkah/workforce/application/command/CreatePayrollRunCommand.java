package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriodId;

import java.util.Objects;

public record CreatePayrollRunCommand(
        TenantId tenantId,
        PayrollPeriodId periodId
) implements Command {
    public CreatePayrollRunCommand {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(periodId, "periodId must not be null");
    }
}
