package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceCycleId;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceReview;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceReviewId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PerformanceReviewRepository extends Repository<PerformanceReview, PerformanceReviewId> {
    CompletionStage<Optional<PerformanceReview>> findByWorkerAndCycle(WorkerId workerId, PerformanceCycleId cycleId);
    CompletionStage<List<PerformanceReview>> findByReviewer(WorkerId reviewerId);
}
