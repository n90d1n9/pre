package tech.kayys.syirkah.event.domain;

import tech.kayys.syirkah.event.domain.valueobject.EventVersion;
import tech.kayys.syirkah.foundation.domain.event.DomainEvent;

/**
 * A business event published on the Syirkah ecosystem bus
 * (base01.md §7, §P1-10).
 *
 * <p>The distinction that matters:
 *
 * <pre>
 *   DomainEvent  - what one bounded context says happened inside itself
 *   BusinessEvent - what the ecosystem promises about that happening
 * </pre>
 *
 * <p>A domain event may stay private forever. A business event is a public
 * contract: it is versioned, tenant-scoped, correlateable, and consumable
 * by a participant that runs none of Syirkah's software.
 */
public interface BusinessEvent extends DomainEvent {

    /**
     * Dotted, stable event name, e.g. {@code logistics.shipment.delivered}.
     */
    String eventType();

    /** Contract version of this event type. */
    EventVersion eventVersion();
}
