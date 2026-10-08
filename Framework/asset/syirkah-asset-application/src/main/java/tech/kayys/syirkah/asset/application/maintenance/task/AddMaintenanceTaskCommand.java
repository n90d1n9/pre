package tech.kayys.syirkah.asset.application.maintenance.task;

import tech.kayys.syirkah.asset.domain.maintenance.task.MaintenanceTask;
import tech.kayys.syirkah.foundation.application.command.Command;
import java.util.Objects;
import java.util.UUID;

public record AddMaintenanceTaskCommand(String tenantId, UUID workOrderId, String taskNumber,
                                        String title, String description, int sequence) implements Command {
    public AddMaintenanceTaskCommand {
        Objects.requireNonNull(tenantId); Objects.requireNonNull(workOrderId);
        Objects.requireNonNull(taskNumber); Objects.requireNonNull(title);
    }
}
