package tech.kayys.syirkah.scheduling.domain.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.ref.ResourceRef;
import tech.kayys.syirkah.scheduling.domain.AvailabilityStatus;
import tech.kayys.syirkah.scheduling.domain.ResourceAvailabilityId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ResourceAvailabilityDeclared(
        UUID eventId,
        Instant occurredAt,
        ResourceAvailabilityId availabilityId,
        ResourceRef resource,
        LocalDate date,
        LocalTime fromTime,
        LocalTime toTime,
        AvailabilityStatus status
) implements DomainEvent {
    public ResourceAvailabilityDeclared(ResourceAvailabilityId availabilityId, ResourceRef resource,
                                        LocalDate date, LocalTime fromTime, LocalTime toTime,
                                        AvailabilityStatus status) {
        this(UUID.randomUUID(), Instant.now(), availabilityId, resource, date, fromTime, toTime, status);
    }
    @Override
    public String eventType() {
        return "scheduling.resource-availability.declared";
    }
}
