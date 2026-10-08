package tech.kayys.syirkah.asset.application.maintenance.workorder;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceWorkOrderRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.util.function.BiConsumer;

public abstract class LifecycleHandler<C> extends AbstractMaintenanceCommandHandler {

    protected LifecycleHandler(MaintenanceWorkOrderRepository repository, EventPublisher eventPublisher,
                               UnitOfWork unitOfWork, DomainClock clock) {
        super(repository, eventPublisher, unitOfWork, clock);
    }

    protected Uni<Result<MaintenanceWorkOrderResult>> transition(String tenantId, java.util.UUID workOrderId,
                                                                 BiConsumer<tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrder, DomainClock> op) {
        return requireWorkOrder(tenantId, workOrderId)
                .flatMap(wo -> {
                    op.accept(wo, clock);
                    return save(tenantId, wo);
                })
                .map(saved -> Result.success(MaintenanceWorkOrderResult.from(saved)));
    }
}
