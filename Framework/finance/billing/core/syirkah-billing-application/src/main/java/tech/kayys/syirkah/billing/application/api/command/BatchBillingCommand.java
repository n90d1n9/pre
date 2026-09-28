package tech.kayys.syirkah.billing.application.api.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import java.time.Instant;

public record BatchBillingCommand(
        String batchId,
        Instant processDate
) implements Command {}
