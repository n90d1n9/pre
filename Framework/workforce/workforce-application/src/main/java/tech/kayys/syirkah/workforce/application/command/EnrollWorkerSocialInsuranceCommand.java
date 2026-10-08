package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.socialinsurance.SocialInsuranceSchemeId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.Objects;

public record EnrollWorkerSocialInsuranceCommand(
        WorkerId workerId,
        EmploymentId employmentId,
        SocialInsuranceSchemeId schemeId,
        String membershipNumber,
        LocalDate effectiveFrom
) implements Command {
    public EnrollWorkerSocialInsuranceCommand {
        Objects.requireNonNull(workerId, "workerId must not be null");
        Objects.requireNonNull(employmentId, "employmentId must not be null");
        Objects.requireNonNull(schemeId, "schemeId must not be null");
        Objects.requireNonNull(membershipNumber, "membershipNumber must not be null");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom must not be null");
    }
}
