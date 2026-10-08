package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.benefit.BenefitId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.Objects;

public record EnrollWorkerBenefitCommand(
        WorkerId workerId,
        EmploymentId employmentId, // optional/nullable
        BenefitId benefitId,
        LocalDate effectiveFrom
) implements Command {
    public EnrollWorkerBenefitCommand {
        Objects.requireNonNull(workerId, "workerId must not be null");
        Objects.requireNonNull(benefitId, "benefitId must not be null");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom must not be null");
    }
}
