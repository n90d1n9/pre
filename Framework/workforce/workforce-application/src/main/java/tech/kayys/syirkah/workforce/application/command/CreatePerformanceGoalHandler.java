package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoal;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoalId;
import tech.kayys.syirkah.workforce.spi.port.PerformanceGoalRepository;

import java.util.Objects;

public class CreatePerformanceGoalHandler implements CommandHandler<CreatePerformanceGoalCommand, Result<PerformanceGoalId>> {

    private final PerformanceGoalRepository repository;
    private final EventPublisher eventPublisher;

    public CreatePerformanceGoalHandler(PerformanceGoalRepository repository, EventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<PerformanceGoalId>> handle(CreatePerformanceGoalCommand cmd) {
        PerformanceGoal goal;
        try {
            goal = PerformanceGoal.create(
                    PerformanceGoalId.generate(),
                    cmd.tenantId(),
                    cmd.cycleId(),
                    cmd.workerId(),
                    cmd.employmentId(),
                    cmd.title(),
                    cmd.description(),
                    cmd.type(),
                    cmd.startDate(),
                    cmd.dueDate(),
                    cmd.targetValue(),
                    cmd.targetUnit()
            );
        } catch (Exception e) {
            return Uni.createFrom().item(Result.failure(ApplicationError.of("INVALID_GOAL", e.getMessage())));
        }
        return Uni.createFrom().completionStage(repository.save(goal))
                .chain(saved -> eventPublisher.publish(saved.pullDomainEvents())
                        .replaceWith(Result.success(saved.getId())));
    }
}
