package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentType;
import tech.kayys.syirkah.workforce.domain.employment.OrganizationRef;
import tech.kayys.syirkah.workforce.domain.employment.PositionRef;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record StartEmploymentCommand(
        WorkerId workerId,
        UUID organizationId,
        UUID positionId,
        EmploymentType type,
        LocalDate startDate
) implements Command {

    public StartEmploymentCommand {
        Objects.requireNonNull(workerId, "workerId cannot be null");
        Objects.requireNonNull(organizationId, "organizationId cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(startDate, "startDate cannot be null");
    }
}
