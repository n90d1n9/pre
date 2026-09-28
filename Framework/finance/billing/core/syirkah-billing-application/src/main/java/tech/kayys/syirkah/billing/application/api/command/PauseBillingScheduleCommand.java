package tech.kayys.syirkah.billing.application.api.command;

import tech.kayys.syirkah.billing.domain.identifier.BillingScheduleId;
import tech.kayys.syirkah.foundation.application.command.Command;

public record PauseBillingScheduleCommand(
        BillingScheduleId scheduleId
) implements Command {}
