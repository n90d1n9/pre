package tech.kayys.syirkah.scheduling.application.command;

import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import java.time.LocalDate;

public record CreateScheduleCommand(
        TenantId tenantId,
        String name,
        LocalDate startDate,
        LocalDate endDate
) {}
