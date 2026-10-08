package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceReviewId;

import java.util.Objects;

public record SubmitPerformanceReviewCommand(
        PerformanceReviewId reviewId,
        String summary
) implements Command {
    public SubmitPerformanceReviewCommand {
        Objects.requireNonNull(reviewId, "reviewId must not be null");
        Objects.requireNonNull(summary, "summary must not be null");
    }
}
