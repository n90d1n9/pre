package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.position.PositionId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.Objects;

public record AssignPositionCommand(
        PositionId positionId,
        WorkerId workerId,
        EmploymentId employmentId,
        LocalDate startDate
) implements Command {

    public AssignPositionCommand {
        Objects.requireNonNull(positionId, "positionId cannot be null");
        Objects.requireNonNull(workerId, "workerId cannot be null");
        Objects.requireNonNull(employmentId, "employmentId cannot be null");
        Objects.requireNonNull(startDate, "startDate cannot be null");
    }
}
