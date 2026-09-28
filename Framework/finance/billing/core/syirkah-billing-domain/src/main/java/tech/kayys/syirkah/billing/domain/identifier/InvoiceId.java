package tech.kayys.syirkah.billing.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class InvoiceId extends Identifier<String> {

    private static final long serialVersionUID = 1L;

    public InvoiceId(String value) {
        super(value);
    }

    public static InvoiceId generate() {
        return new InvoiceId("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    public static InvoiceId of(String value) {
        return new InvoiceId(value);
    }

    @Override
    public String toString() {
        return "InvoiceId{" + value + "}";
    }
}
