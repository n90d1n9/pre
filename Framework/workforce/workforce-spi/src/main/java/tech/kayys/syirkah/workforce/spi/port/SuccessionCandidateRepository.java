package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionCandidate;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionCandidateId;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface SuccessionCandidateRepository extends Repository<SuccessionCandidate, SuccessionCandidateId> {
    CompletionStage<List<SuccessionCandidate>> findByPlan(SuccessionPlanId planId);
    CompletionStage<List<SuccessionCandidate>> findByWorker(WorkerId workerId);
}
