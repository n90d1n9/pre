package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionCandidateId;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionReadinessAssessment;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionReadinessAssessmentId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SuccessionReadinessAssessmentRepository extends Repository<SuccessionReadinessAssessment, SuccessionReadinessAssessmentId> {
    CompletionStage<List<SuccessionReadinessAssessment>> findByCandidate(SuccessionCandidateId candidateId);
}
