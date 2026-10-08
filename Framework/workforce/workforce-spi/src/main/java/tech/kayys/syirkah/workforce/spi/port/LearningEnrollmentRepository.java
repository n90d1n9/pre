package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollment;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;
import tech.kayys.syirkah.workforce.domain.learning.LearningOfferingId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface LearningEnrollmentRepository extends Repository<LearningEnrollment, LearningEnrollmentId> {
    CompletionStage<Optional<LearningEnrollment>> findByWorkerAndOffering(WorkerId workerId, LearningOfferingId offeringId);
    CompletionStage<List<LearningEnrollment>> findByWorker(WorkerId workerId);
    CompletionStage<List<LearningEnrollment>> findByOffering(LearningOfferingId offeringId);
}
