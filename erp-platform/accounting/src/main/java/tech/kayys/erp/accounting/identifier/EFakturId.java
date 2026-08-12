
import java.util.UUID;

public final class EFakturId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public EFakturId(UUID value) {
        super(value);
    }

    public static EFakturId of(UUID value) {
        return new EFakturId(value);
    }

    public static EFakturId generate() {
        return new EFakturId(UUID.randomUUID());
    }

    public static EFakturId fromString(String value) {
        return new EFakturId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "EFakturId{" + value + "}";
    }
}