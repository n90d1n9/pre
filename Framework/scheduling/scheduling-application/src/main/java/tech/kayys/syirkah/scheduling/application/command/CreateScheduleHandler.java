package tech.kayys.syirkah.scheduling.application.command;

import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.scheduling.domain.Schedule;
import tech.kayys.syirkah.scheduling.domain.ScheduleId;
import tech.kayys.syirkah.scheduling.spi.ScheduleRepository;

import java.util.Objects;
import java.util.concurrent.CompletionStage;

public class CreateScheduleHandler {

    private final ScheduleRepository scheduleRepository;

    public CreateScheduleHandler(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
    }

    public CompletionStage<Result<ScheduleId>> handle(CreateScheduleCommand cmd) {
        ScheduleId id = ScheduleId.generate();
        Schedule schedule = Schedule.create(id, cmd.tenantId(), cmd.name(),
                cmd.startDate(), cmd.endDate());
        return scheduleRepository.save(schedule)
                .thenApply(saved -> Result.success(id));
    }
}
