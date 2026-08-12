package tech.kayys.erp.accounting.identifier;


import java.util.UUID;

/**
 * Journal entry identifier.
 */
public final class JournalEntryId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public JournalEntryId(UUID value) {
        super(value);
    }

    public static JournalEntryId of(UUID value) {
        return new JournalEntryId(value);
    }

    public static JournalEntryId generate() {
        return new JournalEntryId(UUID.randomUUID());
    }

    public static JournalEntryId fromString(String value) {
        return new JournalEntryId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "JournalEntryId{" + value + "}";
    }
}