package tech.kayys.syirkah.asset.application.maintenance.task;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.event.MaintenanceTaskAdded;
import tech.kayys.syirkah.asset.domain.event.MaintenanceTaskCompleted;
import tech.kayys.syirkah.asset.domain.maintenance.task.MaintenanceTask;
import tech.kayys.syirkah.asset.domain.maintenance.task.MaintenanceTaskId;
import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrderId;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceTaskRepository;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceWorkOrderRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.ApplicationErrorException;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AddMaintenanceTaskHandler implements CommandHandler<AddMaintenanceTaskCommand, Result<MaintenanceTaskResult>> {

    private final MaintenanceWorkOrderRepository workOrders;
    private final MaintenanceTaskRepository tasks;
    private final EventPublisher eventPublisher;
    private final UnitOfWork unitOfWork;
    private final DomainClock clock;

    public AddMaintenanceTaskHandler(MaintenanceWorkOrderRepository workOrders, MaintenanceTaskRepository tasks,
                                     EventPublisher eventPublisher, UnitOfWork unitOfWork, DomainClock clock) {
        this.workOrders = Objects.requireNonNull(workOrders);
        this.tasks = Objects.requireNonNull(tasks);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.unitOfWork = Objects.requireNonNull(unitOfWork);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public Uni<Result<MaintenanceTaskResult>> handle(AddMaintenanceTaskCommand cmd) {
        return Uni.createFrom().completionStage(() -> workOrders.findById(cmd.tenantId(), MaintenanceWorkOrderId.of(cmd.workOrderId())))
                .map(opt -> opt.orElse(null))
                .onItem().ifNull().failWith(() -> new ApplicationErrorException(
                        ApplicationError.of("maintenance.work-order.not-found", "Work order not found: " + cmd.workOrderId())))
                .flatMap(wo -> {
                    MaintenanceTask task = MaintenanceTask.create(MaintenanceTaskId.generate(),
                            cmd.tenantId(), cmd.workOrderId(), cmd.taskNumber(), cmd.title(),
                            cmd.description(), cmd.sequence(), clock.now());
                    return unitOfWork.execute(() -> Uni.createFrom()
                            .completionStage(() -> tasks.save(cmd.tenantId(), task))
                            .flatMap(saved -> eventPublisher.publish(List.of(new MaintenanceTaskAdded(
                                    UUID.randomUUID(), clock.now(), cmd.workOrderId(), saved.id().value(), saved.taskNumber())))
                                    .replaceWith(saved)));
                })
                .map(saved -> Result.success(MaintenanceTaskResult.from(saved)));
    }
}
