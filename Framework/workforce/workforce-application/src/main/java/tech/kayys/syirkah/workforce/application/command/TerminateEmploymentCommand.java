package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;

import java.time.LocalDate;
import java.util.Objects;

public record TerminateEmploymentCommand(
        EmploymentId employmentId,
        LocalDate effectiveDate,
        String reason
) implements Command {

    public TerminateEmploymentCommand {
        Objects.requireNonNull(employmentId, "employmentId cannot be null");
        Objects.requireNonNull(effectiveDate, "effectiveDate cannot be null");
        Objects.requireNonNull(reason, "reason cannot be null");
    }
}
