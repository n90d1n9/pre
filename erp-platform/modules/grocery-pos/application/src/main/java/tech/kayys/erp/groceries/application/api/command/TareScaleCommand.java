package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;

public record TareScaleCommand(ScaleId scaleId) implements Command<ScaleId> {
    public TareScaleCommand {
        if (scaleId == null) throw new IllegalArgumentException("Scale ID cannot be null");
    }
}
