package tech.kayys.syirkah.asset.application.maintenance.workorder;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceWorkOrderRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

public class CompleteMaintenanceWorkOrderHandler extends LifecycleHandler<CompleteMaintenanceWorkOrderCommand>
        implements CommandHandler<CompleteMaintenanceWorkOrderCommand, Result<MaintenanceWorkOrderResult>> {
    public CompleteMaintenanceWorkOrderHandler(MaintenanceWorkOrderRepository r, EventPublisher e, UnitOfWork u, DomainClock c) { super(r, e, u, c); }
    @Override public Uni<Result<MaintenanceWorkOrderResult>> handle(CompleteMaintenanceWorkOrderCommand cmd) {
        return transition(cmd.tenantId(), cmd.workOrderId(), (wo, clock) -> wo.complete(clock));
    }
}
