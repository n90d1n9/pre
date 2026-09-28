package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.worker.WorkerType;

import java.util.Objects;
import java.util.UUID;

/**
 * Registers a new worker in the workforce system.
 */
public record RegisterWorkerCommand(
        UUID personId,
        WorkerType workerType,
        String actor
) implements Command {

    public RegisterWorkerCommand {
        Objects.requireNonNull(personId, "personId cannot be null");
        Objects.requireNonNull(workerType, "workerType cannot be null");
    }
}
