package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.socialinsurance.WorkerSocialInsuranceEnrollment;
import tech.kayys.syirkah.workforce.domain.socialinsurance.WorkerSocialInsuranceEnrollmentId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WorkerSocialInsuranceEnrollmentRepository extends Repository<WorkerSocialInsuranceEnrollment, WorkerSocialInsuranceEnrollmentId> {

    CompletionStage<List<WorkerSocialInsuranceEnrollment>> findByWorker(WorkerId workerId);

    CompletionStage<List<WorkerSocialInsuranceEnrollment>> findByEmployment(EmploymentId employmentId);
}
