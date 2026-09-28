package tech.kayys.syirkah.finance.treasury.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;
import java.util.UUID;

public final class DrawerSessionId extends Identifier<UUID> {
    private static final long serialVersionUID = 1L;

    public DrawerSessionId(UUID value) { super(value); }
    public static DrawerSessionId of(UUID value) { return new DrawerSessionId(value); }
    public static DrawerSessionId generate() { return new DrawerSessionId(UUID.randomUUID()); }
    public static DrawerSessionId fromString(String val) { return new DrawerSessionId(UUID.fromString(val)); }
}
