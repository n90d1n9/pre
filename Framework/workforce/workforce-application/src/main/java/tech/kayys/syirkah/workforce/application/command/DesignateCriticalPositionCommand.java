package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.position.PositionId;
import tech.kayys.syirkah.workforce.domain.talent.Criticality;

import java.util.Objects;

public record DesignateCriticalPositionCommand(
        TenantId tenantId,
        PositionId positionId,
        Criticality criticality,
        String reason
) implements Command {
    public DesignateCriticalPositionCommand {
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(positionId, "positionId must not be null");
        Objects.requireNonNull(criticality, "criticality must not be null");
    }
}
