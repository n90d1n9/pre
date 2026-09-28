package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.timesheet.Timesheet;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetStatus;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TimesheetRepository extends Repository<Timesheet, TimesheetId> {

    CompletionStage<Optional<Timesheet>> findByWorkerAndPeriod(
            WorkerId workerId, EmploymentId employmentId, LocalDate periodStart, LocalDate periodEnd);

    CompletionStage<List<Timesheet>> findByWorkerId(WorkerId workerId);

    CompletionStage<List<Timesheet>> findByStatus(TimesheetStatus status);
}
