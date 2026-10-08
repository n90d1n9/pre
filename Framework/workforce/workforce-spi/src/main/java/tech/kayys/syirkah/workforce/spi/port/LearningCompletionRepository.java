package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningCompletion;
import tech.kayys.syirkah.workforce.domain.learning.LearningCompletionId;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface LearningCompletionRepository extends Repository<LearningCompletion, LearningCompletionId> {
    CompletionStage<Optional<LearningCompletion>> findByEnrollment(LearningEnrollmentId enrollmentId);
}
