package tech.kayys.syirkah.asset.domain.event;
import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MaintenanceWorkOrderResumed(UUID eventId, Instant occurredAt, UUID workOrderId, UUID assetId) implements DomainEvent {
    public MaintenanceWorkOrderResumed { Objects.requireNonNull(eventId); Objects.requireNonNull(occurredAt); Objects.requireNonNull(workOrderId); Objects.requireNonNull(assetId); }
    @Override public String eventType() { return "maintenance.work-order-resumed"; }
}
