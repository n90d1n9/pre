package tech.kayys.erp.billing;



import java.util.UUID;

/**
 * Invoice identifier.
 */
public final class InvoiceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public InvoiceId(UUID value) {
        super(value);
    }

    public static InvoiceId of(UUID value) {
        return new InvoiceId(value);
    }

    public static InvoiceId generate() {
        return new InvoiceId(UUID.randomUUID());
    }

    public static InvoiceId fromString(String value) {
        return new InvoiceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "InvoiceId{" + value + "}";
    }
}
