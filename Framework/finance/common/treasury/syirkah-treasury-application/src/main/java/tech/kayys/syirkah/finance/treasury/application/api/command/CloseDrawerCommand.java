package tech.kayys.syirkah.finance.treasury.application.api.command;

import tech.kayys.syirkah.finance.treasury.domain.identifier.DrawerSessionId;
import tech.kayys.syirkah.foundation.application.command.Command;
import java.math.BigDecimal;

public record CloseDrawerCommand(
        DrawerSessionId sessionId,
        BigDecimal actualCountedCash,
        String notes
) implements Command {
}
