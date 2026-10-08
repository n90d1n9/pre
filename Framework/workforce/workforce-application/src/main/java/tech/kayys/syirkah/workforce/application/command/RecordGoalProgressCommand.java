package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoalId;

import java.math.BigDecimal;
import java.util.Objects;

public record RecordGoalProgressCommand(
        PerformanceGoalId goalId,
        BigDecimal progress
) implements Command {
    public RecordGoalProgressCommand {
        Objects.requireNonNull(goalId, "goalId must not be null");
        Objects.requireNonNull(progress, "progress must not be null");
    }
}
