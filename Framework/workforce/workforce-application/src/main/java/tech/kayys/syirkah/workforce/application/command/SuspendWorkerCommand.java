package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Objects;

public record SuspendWorkerCommand(
        WorkerId workerId,
        String actor,
        String reason
) implements Command {

    public SuspendWorkerCommand {
        Objects.requireNonNull(workerId, "workerId cannot be null");
        Objects.requireNonNull(reason, "reason cannot be null");
    }
}
