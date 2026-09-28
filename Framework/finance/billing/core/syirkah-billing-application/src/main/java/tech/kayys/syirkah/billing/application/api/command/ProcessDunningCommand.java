package tech.kayys.syirkah.billing.application.api.command;

import tech.kayys.syirkah.billing.domain.valueobject.DunningAction;
import tech.kayys.syirkah.foundation.application.command.Command;

public record ProcessDunningCommand(
        String customerId,
        int daysOverdue,
        DunningAction action
) implements Command {
    public ProcessDunningCommand(String customerId) {
        this(customerId, 0, null);
    }
    public ProcessDunningCommand(int daysOverdue, DunningAction action) {
        this(null, daysOverdue, action);
    }
}
