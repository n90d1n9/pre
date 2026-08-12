
import java.util.UUID;

public final class BudgetId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public BudgetId(UUID value) {
        super(value);
    }

    public static BudgetId of(UUID value) {
        return new BudgetId(value);
    }

    public static BudgetId generate() {
        return new BudgetId(UUID.randomUUID());
    }

    public static BudgetId fromString(String value) {
        return new BudgetId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "BudgetId{" + value + "}";
    }
}