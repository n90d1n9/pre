package tech.kayys.syirkah.asset.application.maintenance.task;

import tech.kayys.syirkah.foundation.application.command.Command;
import java.util.Objects;
import java.util.UUID;

public record CompleteMaintenanceTaskCommand(String tenantId, UUID taskId) implements Command {
    public CompleteMaintenanceTaskCommand { Objects.requireNonNull(tenantId); Objects.requireNonNull(taskId); }
}
