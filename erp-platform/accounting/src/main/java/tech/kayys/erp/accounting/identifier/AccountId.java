package tech.kayys.erp.accounting.identifier;


import java.util.UUID;

/**
 * Account identifier for the chart of accounts.
 */
public final class AccountId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AccountId(UUID value) {
        super(value);
    }

    public static AccountId of(UUID value) {
        return new AccountId(value);
    }

    public static AccountId generate() {
        return new AccountId(UUID.randomUUID());
    }

    public static AccountId fromString(String value) {
        return new AccountId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AccountId{" + value + "}";
    }
}