package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceCycleId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Objects;

public record CreatePerformanceReviewCommand(
        PerformanceCycleId cycleId,
        WorkerId workerId,
        EmploymentId employmentId,
        WorkerId reviewerWorkerId
) implements Command {
    public CreatePerformanceReviewCommand {
        Objects.requireNonNull(cycleId, "cycleId must not be null");
        Objects.requireNonNull(workerId, "workerId must not be null");
        Objects.requireNonNull(employmentId, "employmentId must not be null");
        Objects.requireNonNull(reviewerWorkerId, "reviewerWorkerId must not be null");
    }
}
