package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRun;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;
import tech.kayys.syirkah.workforce.spi.port.PayrollPeriodRepository;
import tech.kayys.syirkah.workforce.spi.port.PayrollRunRepository;

import java.time.Instant;
import java.util.Objects;

public class CreatePayrollRunHandler implements CommandHandler<CreatePayrollRunCommand, Result<PayrollRunId>> {

    private final PayrollPeriodRepository periodRepository;
    private final PayrollRunRepository runRepository;
    private final EventPublisher eventPublisher;

    public CreatePayrollRunHandler(
            PayrollPeriodRepository periodRepository,
            PayrollRunRepository runRepository,
            EventPublisher eventPublisher
    ) {
        this.periodRepository = Objects.requireNonNull(periodRepository);
        this.runRepository = Objects.requireNonNull(runRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<PayrollRunId>> handle(CreatePayrollRunCommand cmd) {
        return Uni.createFrom().completionStage(periodRepository.findById(cmd.periodId()))
                .chain(optPeriod -> {
                    if (optPeriod.isEmpty()) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("PAYROLL_PERIOD_NOT_FOUND", "Payroll period not found")));
                    }
                    PayrollRun run;
                    try {
                        run = PayrollRun.create(
                                PayrollRunId.generate(),
                                cmd.periodId(),
                                cmd.tenantId(),
                                Instant.now()
                        );
                    } catch (Exception e) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("INVALID_PAYROLL_RUN", e.getMessage())));
                    }
                    return Uni.createFrom().completionStage(runRepository.save(run))
                            .chain(saved -> eventPublisher.publish(saved.pullDomainEvents())
                                    .replaceWith(Result.success(saved.getId())));
                });
    }
}
