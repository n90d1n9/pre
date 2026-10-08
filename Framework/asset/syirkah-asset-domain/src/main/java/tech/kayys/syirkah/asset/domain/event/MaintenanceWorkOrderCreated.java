package tech.kayys.syirkah.asset.domain.event;
import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record MaintenanceWorkOrderCreated(UUID eventId, Instant occurredAt, UUID workOrderId, UUID assetId, String workOrderNumber) implements DomainEvent {
    public MaintenanceWorkOrderCreated { Objects.requireNonNull(eventId); Objects.requireNonNull(occurredAt); Objects.requireNonNull(workOrderId); Objects.requireNonNull(assetId); Objects.requireNonNull(workOrderNumber); }
    @Override public String eventType() { return "maintenance.work-order-created"; }
}
