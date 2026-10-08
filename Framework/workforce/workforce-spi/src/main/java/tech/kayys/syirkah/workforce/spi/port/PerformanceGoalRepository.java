package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceCycleId;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoal;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceGoalId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PerformanceGoalRepository extends Repository<PerformanceGoal, PerformanceGoalId> {
    CompletionStage<List<PerformanceGoal>> findByWorkerAndCycle(WorkerId workerId, PerformanceCycleId cycleId);
}
