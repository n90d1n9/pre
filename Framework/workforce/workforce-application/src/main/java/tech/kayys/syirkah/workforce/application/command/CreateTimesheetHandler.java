package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.timesheet.Timesheet;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.spi.port.TimesheetRepository;

import java.util.Objects;

public class CreateTimesheetHandler implements CommandHandler<CreateTimesheetCommand, Result<TimesheetId>> {

    private final TimesheetRepository timesheetRepository;
    private final EventPublisher eventPublisher;

    public CreateTimesheetHandler(TimesheetRepository timesheetRepository, EventPublisher eventPublisher) {
        this.timesheetRepository = Objects.requireNonNull(timesheetRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<TimesheetId>> handle(CreateTimesheetCommand cmd) {
        TimesheetId id = TimesheetId.generate();
        Timesheet timesheet = Timesheet.create(id, cmd.workerId(), cmd.employmentId(), cmd.periodStart(), cmd.periodEnd());
        return Uni.createFrom().completionStage(timesheetRepository.save(timesheet))
                .chain(saved -> eventPublisher.publish(saved.pullDomainEvents())
                        .replaceWith(Result.success(id)));
    }
}
