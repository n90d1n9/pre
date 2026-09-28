package tech.kayys.syirkah.finance.treasury.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;
import java.util.UUID;

public final class CashMovementId extends Identifier<UUID> {
    private static final long serialVersionUID = 1L;

    public CashMovementId(UUID value) { super(value); }
    public static CashMovementId of(UUID value) { return new CashMovementId(value); }
    public static CashMovementId generate() { return new CashMovementId(UUID.randomUUID()); }
}
