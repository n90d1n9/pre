
import java.util.UUID;

public final class WithholdingTaxId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WithholdingTaxId(UUID value) {
        super(value);
    }

    public static WithholdingTaxId of(UUID value) {
        return new WithholdingTaxId(value);
    }

    public static WithholdingTaxId generate() {
        return new WithholdingTaxId(UUID.randomUUID());
    }

    public static WithholdingTaxId fromString(String value) {
        return new WithholdingTaxId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WithholdingTaxId{" + value + "}";
    }
}