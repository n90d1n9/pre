package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class CustomerPortalUserId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CustomerPortalUserId(UUID value) {
        super(value);
    }

    public static CustomerPortalUserId of(UUID value) {
        return new CustomerPortalUserId(value);
    }

    public static CustomerPortalUserId generate() {
        return new CustomerPortalUserId(UUID.randomUUID());
    }

    public static CustomerPortalUserId fromString(String value) {
        return new CustomerPortalUserId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CustomerPortalUserId{" + value + "}";
    }
}
