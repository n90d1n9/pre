package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.benefit.WorkerBenefitEnrollment;
import tech.kayys.syirkah.workforce.domain.benefit.WorkerBenefitEnrollmentId;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WorkerBenefitEnrollmentRepository extends Repository<WorkerBenefitEnrollment, WorkerBenefitEnrollmentId> {

    CompletionStage<List<WorkerBenefitEnrollment>> findByWorker(WorkerId workerId);

    CompletionStage<List<WorkerBenefitEnrollment>> findByEmployment(EmploymentId employmentId);

    CompletionStage<List<WorkerBenefitEnrollment>> findByWorkerAndEffectiveDate(WorkerId workerId, LocalDate date);
}
