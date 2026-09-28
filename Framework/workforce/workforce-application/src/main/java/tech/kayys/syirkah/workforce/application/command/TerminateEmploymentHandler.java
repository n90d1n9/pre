package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.spi.port.EmploymentRepository;

import java.util.Objects;

public final class TerminateEmploymentHandler
        implements CommandHandler<TerminateEmploymentCommand, Result<EmploymentId>> {

    private final EmploymentRepository employmentRepository;
    private final EventPublisher eventPublisher;

    public TerminateEmploymentHandler(
            EmploymentRepository employmentRepository,
            EventPublisher eventPublisher) {
        this.employmentRepository = Objects.requireNonNull(employmentRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<EmploymentId>> handle(TerminateEmploymentCommand command) {
        return Uni.createFrom()
                .completionStage(employmentRepository.findById(command.employmentId()))
                .onItem()
                .transformToUni(opt -> {
                    if (opt.isEmpty()) {
                        return Uni.createFrom().item(
                                Result.failure(
                                        ApplicationError.of(
                                                "EMPLOYMENT_NOT_FOUND",
                                                "Employment not found: " + command.employmentId().value()
                                        )
                                )
                        );
                    }

                    var employment = opt.get();
                    try {
                        employment.terminate(command.effectiveDate(), command.reason());
                    } catch (Exception e) {
                        return Uni.createFrom().item(
                                Result.failure(ApplicationError.of("TERMINATION_FAILED", e.getMessage()))
                        );
                    }

                    return Uni.createFrom()
                            .completionStage(employmentRepository.save(employment))
                            .onItem()
                            .transformToUni(saved -> eventPublisher
                                    .publish(saved.pullDomainEvents())
                                    .replaceWith(Result.success(saved.id())));
                });
    }
}
