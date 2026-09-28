package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;

import java.util.Objects;

public record ApprovePayrollRunCommand(
        PayrollRunId runId
) implements Command {
    public ApprovePayrollRunCommand {
        Objects.requireNonNull(runId, "runId must not be null");
    }
}
