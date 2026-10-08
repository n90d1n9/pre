package tech.kayys.syirkah.asset.application.maintenance.workorder;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.maintenance.AssetReference;
import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrder;
import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrderId;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceWorkOrderRepository;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Shared maintenance orchestration: require, save-with-outbox inside UnitOfWork. */
public abstract class AbstractMaintenanceCommandHandler {

    protected final MaintenanceWorkOrderRepository repository;
    protected final EventPublisher eventPublisher;
    protected final UnitOfWork unitOfWork;
    protected final DomainClock clock;

    protected AbstractMaintenanceCommandHandler(MaintenanceWorkOrderRepository repository,
                                                EventPublisher eventPublisher,
                                                UnitOfWork unitOfWork,
                                                DomainClock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    protected Uni<MaintenanceWorkOrder> requireWorkOrder(String tenantId, UUID workOrderId) {
        return Uni.createFrom()
                .completionStage(() -> repository.findById(tenantId, MaintenanceWorkOrderId.of(workOrderId)))
                .map(opt -> opt.orElse(null))
                .onItem().ifNull().failWith(() -> new tech.kayys.syirkah.foundation.application.result.ApplicationErrorException(
                        ApplicationError.of("maintenance.work-order.not-found", "Work order not found: " + workOrderId)));
    }

    protected Uni<MaintenanceWorkOrder> save(String tenantId, MaintenanceWorkOrder workOrder) {
        return unitOfWork.execute(() -> Uni.createFrom()
                .completionStage(() -> repository.save(tenantId, workOrder))
                .flatMap(saved -> publishPendingEvents(saved).replaceWith(saved)));
    }

    protected Uni<Void> publishPendingEvents(MaintenanceWorkOrder workOrder) {
        List<DomainEvent> events = workOrder.pullDomainEvents();
        if (events.isEmpty()) return Uni.createFrom().nullItem();
        return eventPublisher.publish(events);
    }

    protected static Uni<Void> requireAsset(AssetReference assetReference, String tenantId, UUID assetId) {
        return Uni.createFrom().completionStage(() -> assetReference.assetExists(tenantId, assetId))
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Uni.createFrom().nullItem()
                        : Uni.createFrom().failure(new tech.kayys.syirkah.foundation.application.result.ApplicationErrorException(
                                ApplicationError.of("asset.reference.not-found", "Asset not found: " + assetId))));
    }
}
