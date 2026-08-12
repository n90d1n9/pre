package tech.kayys.erp.groceries.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;

public record ReadWeightCommand(ScaleId scaleId) implements Command<tech.kayys.erp.groceries.domain.valueobject.Weight> {
    public ReadWeightCommand {
        if (scaleId == null) throw new IllegalArgumentException("Scale ID cannot be null");
    }
}
