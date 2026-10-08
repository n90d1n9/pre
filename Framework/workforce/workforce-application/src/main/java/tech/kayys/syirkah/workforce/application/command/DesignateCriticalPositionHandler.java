package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.talent.CriticalPosition;
import tech.kayys.syirkah.workforce.domain.talent.CriticalPositionId;
import tech.kayys.syirkah.workforce.spi.port.CriticalPositionRepository;

import java.util.Objects;

public class DesignateCriticalPositionHandler implements CommandHandler<DesignateCriticalPositionCommand, Result<CriticalPositionId>> {

    private final CriticalPositionRepository repository;
    private final EventPublisher eventPublisher;

    public DesignateCriticalPositionHandler(CriticalPositionRepository repository, EventPublisher eventPublisher) {
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<CriticalPositionId>> handle(DesignateCriticalPositionCommand cmd) {
        return Uni.createFrom().completionStage(repository.findByPosition(cmd.positionId()))
                .chain(optExisting -> {
                    if (optExisting.isPresent()) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("ALREADY_DESIGNATED", "Position is already designated as critical")));
                    }
                    CriticalPosition cp;
                    try {
                        cp = CriticalPosition.designate(
                                CriticalPositionId.generate(),
                                cmd.tenantId(),
                                cmd.positionId(),
                                cmd.criticality(),
                                cmd.reason()
                        );
                    } catch (Exception e) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("DESIGNATE_FAILED", e.getMessage())));
                    }
                    return Uni.createFrom().completionStage(repository.save(cp))
                            .chain(saved -> eventPublisher.publish(saved.pullDomainEvents())
                                    .replaceWith(Result.success(saved.getId())));
                });
    }
}
