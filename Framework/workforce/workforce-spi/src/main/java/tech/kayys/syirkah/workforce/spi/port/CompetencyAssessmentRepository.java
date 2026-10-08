package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.performance.CompetencyAssessment;
import tech.kayys.syirkah.workforce.domain.performance.CompetencyAssessmentId;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceCycleId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface CompetencyAssessmentRepository extends Repository<CompetencyAssessment, CompetencyAssessmentId> {
    CompletionStage<List<CompetencyAssessment>> findByWorkerAndCycle(WorkerId workerId, PerformanceCycleId cycleId);
}
