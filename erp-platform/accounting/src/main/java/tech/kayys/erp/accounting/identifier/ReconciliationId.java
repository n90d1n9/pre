
import java.util.UUID;

public final class ReconciliationId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ReconciliationId(UUID value) {
        super(value);
    }

    public static ReconciliationId of(UUID value) {
        return new ReconciliationId(value);
    }

    public static ReconciliationId generate() {
        return new ReconciliationId(UUID.randomUUID());
    }

    public static ReconciliationId fromString(String value) {
        return new ReconciliationId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ReconciliationId{" + value + "}";
    }
}
