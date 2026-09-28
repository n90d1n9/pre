package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.attendance.AttendanceRecord;
import tech.kayys.syirkah.workforce.domain.attendance.AttendanceRecordId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface AttendanceRecordRepository extends Repository<AttendanceRecord, AttendanceRecordId> {

    CompletionStage<Optional<AttendanceRecord>> findByWorkerAndDate(
            WorkerId workerId, EmploymentId employmentId, LocalDate workDate);

    CompletionStage<List<AttendanceRecord>> findByWorkerAndDateRange(
            WorkerId workerId, EmploymentId employmentId, LocalDate startDate, LocalDate endDate);
}
