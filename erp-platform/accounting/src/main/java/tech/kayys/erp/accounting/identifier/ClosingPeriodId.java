
import java.util.UUID;

public final class ClosingPeriodId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ClosingPeriodId(UUID value) {
        super(value);
    }

    public static ClosingPeriodId of(UUID value) {
        return new ClosingPeriodId(value);
    }

    public static ClosingPeriodId generate() {
        return new ClosingPeriodId(UUID.randomUUID());
    }

    public static ClosingPeriodId fromString(String value) {
        return new ClosingPeriodId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ClosingPeriodId{" + value + "}";
    }
}