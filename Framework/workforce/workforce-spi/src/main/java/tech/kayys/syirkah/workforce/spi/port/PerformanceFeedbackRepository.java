package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceFeedback;
import tech.kayys.syirkah.workforce.domain.performance.PerformanceFeedbackId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PerformanceFeedbackRepository extends Repository<PerformanceFeedback, PerformanceFeedbackId> {
    CompletionStage<List<PerformanceFeedback>> findByWorker(WorkerId workerId);
}
