package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Objects;

public record ActivateWorkerCommand(
        WorkerId workerId,
        String actor
) implements Command {

    public ActivateWorkerCommand {
        Objects.requireNonNull(workerId, "workerId cannot be null");
    }
}
