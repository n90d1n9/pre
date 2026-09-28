package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public record ClockOutCommand(
        WorkerId workerId,
        EmploymentId employmentId,
        LocalDate workDate,
        LocalDateTime timestamp
) implements Command {
    public ClockOutCommand {
        Objects.requireNonNull(workerId, "workerId cannot be null");
        Objects.requireNonNull(employmentId, "employmentId cannot be null");
        Objects.requireNonNull(workDate, "workDate cannot be null");
        Objects.requireNonNull(timestamp, "timestamp cannot be null");
    }
}
