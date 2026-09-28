package tech.kayys.syirkah.event.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

/**
 * Stable identity of a single business event occurrence.
 *
 * <p>Consumers use this to deduplicate redelivery (see the inbox / idempotency
 * requirement in base01.md §P1-15): the same EventId arriving twice must be
 * processed once.
 */
public final class EventId extends Identifier<UUID> {

    private static final long serialVersionUID = 1L;

    public EventId(UUID value) {
        super(value);
    }

    public static EventId of(UUID value) {
        return new EventId(value);
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID());
    }

    public static EventId fromString(String value) {
        return new EventId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "EventId{" + value + "}";
    }
}
