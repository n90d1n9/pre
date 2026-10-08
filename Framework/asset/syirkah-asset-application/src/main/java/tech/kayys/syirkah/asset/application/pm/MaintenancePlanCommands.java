package tech.kayys.syirkah.asset.application.pm;

import tech.kayys.syirkah.asset.domain.maintenance.plan.MaintenanceRule;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.util.List;

public final class MaintenancePlanCommands {
    private MaintenancePlanCommands() {}

    public record CreateMaintenancePlanCommand(
            String tenantId, String planNumber, String name, String description,
            List<MaintenanceRule> rules) implements Command {}

    public record ActivateMaintenancePlanCommand(String tenantId, String planId) implements Command {}

    public record SuspendMaintenancePlanCommand(String tenantId, String planId) implements Command {}

    public record RetireMaintenancePlanCommand(String tenantId, String planId) implements Command {}
}
