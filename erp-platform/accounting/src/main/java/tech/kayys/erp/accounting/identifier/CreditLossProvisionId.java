
import java.util.UUID;

public final class CreditLossProvisionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CreditLossProvisionId(UUID value) {
        super(value);
    }

    public static CreditLossProvisionId of(UUID value) {
        return new CreditLossProvisionId(value);
    }

    public static CreditLossProvisionId generate() {
        return new CreditLossProvisionId(UUID.randomUUID());
    }

    public static CreditLossProvisionId fromString(String value) {
        return new CreditLossProvisionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CreditLossProvisionId{" + value + "}";
    }
}