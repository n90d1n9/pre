package tech.kayys.syirkah.reliability.spi.port;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.reliability.inbox.InboxArrival;
import tech.kayys.syirkah.reliability.inbox.InboxEntry;

/**
 * Deduplication store consulted before the domain sees an event
 * (base01.md §P1-15).
 */
public interface InboxStorePort {

    /**
     * Records the arrival of an event id if it has not been seen.
     *
     * @return the entry, plus whether this call was the first arrival
     */
    Uni<InboxArrival> recordArrival(String eventId, String eventType);

    Uni<Boolean> alreadyHandled(String eventId);

    Uni<Void> markHandled(InboxEntry entry);

    Uni<InboxEntry> findById(java.util.UUID entryId);
}
