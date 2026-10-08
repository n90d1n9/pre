package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningAssessment;
import tech.kayys.syirkah.workforce.domain.learning.LearningAssessmentId;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface LearningAssessmentRepository extends Repository<LearningAssessment, LearningAssessmentId> {
    CompletionStage<List<LearningAssessment>> findByEnrollment(LearningEnrollmentId enrollmentId);
}
