package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class AccountId extends Identifier<UUID> {

    public AccountId(UUID value) {
        super(value);
    }

    public static AccountId of(UUID value) {
        return new AccountId(value);
    }

    public static AccountId generate() {
        return of(UUID.randomUUID());
    }
}
