package tech.kayys.syirkah.finance.treasury.domain.model;

import tech.kayys.syirkah.finance.treasury.domain.identifier.CashMovementId;
import tech.kayys.syirkah.finance.treasury.domain.identifier.DrawerSessionId;
import tech.kayys.syirkah.finance.treasury.domain.valueobject.CashMovementType;
import tech.kayys.syirkah.foundation.domain.valueobject.ValueObject;

import java.math.BigDecimal;
import java.time.Instant;

public record CashMovement(
        CashMovementId movementId,
        DrawerSessionId sessionId,
        CashMovementType type,
        BigDecimal amount,
        String currencyCode,
        String reason,
        String authorizedBy,
        Instant occurredAt
) implements ValueObject {
}
