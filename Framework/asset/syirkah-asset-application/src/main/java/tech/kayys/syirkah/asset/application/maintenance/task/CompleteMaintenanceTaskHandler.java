package tech.kayys.syirkah.asset.application.maintenance.task;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.event.MaintenanceTaskCompleted;
import tech.kayys.syirkah.asset.domain.maintenance.task.MaintenanceTaskId;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceTaskRepository;
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

public class CompleteMaintenanceTaskHandler implements CommandHandler<CompleteMaintenanceTaskCommand, Result<MaintenanceTaskResult>> {

    private final MaintenanceTaskRepository tasks;
    private final EventPublisher eventPublisher;
    private final UnitOfWork unitOfWork;
    private final DomainClock clock;

    public CompleteMaintenanceTaskHandler(MaintenanceTaskRepository tasks, EventPublisher eventPublisher,
                                          UnitOfWork unitOfWork, DomainClock clock) {
        this.tasks = Objects.requireNonNull(tasks);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.unitOfWork = Objects.requireNonNull(unitOfWork);
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public Uni<Result<MaintenanceTaskResult>> handle(CompleteMaintenanceTaskCommand cmd) {
        return Uni.createFrom().completionStage(() -> tasks.findById(cmd.tenantId(), MaintenanceTaskId.of(cmd.taskId())))
                .map(opt -> opt.orElse(null))
                .onItem().ifNull().failWith(() -> new ApplicationErrorException(
                        ApplicationError.of("maintenance.task.not-found", "Task not found: " + cmd.taskId())))
                .flatMap(task -> {
                    var completed = task.complete(clock.now());
                    return unitOfWork.execute(() -> Uni.createFrom()
                            .completionStage(() -> tasks.save(cmd.tenantId(), completed))
                            .flatMap(saved -> eventPublisher.publish(List.of(new MaintenanceTaskCompleted(
                                    UUID.randomUUID(), clock.now(), saved.workOrderId(), saved.id().value())))
                                    .replaceWith(saved)));
                })
                .map(saved -> Result.success(MaintenanceTaskResult.from(saved)));
    }
}
