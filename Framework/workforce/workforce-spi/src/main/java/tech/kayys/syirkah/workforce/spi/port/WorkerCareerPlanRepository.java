package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.development.WorkerCareerPlan;
import tech.kayys.syirkah.workforce.domain.development.WorkerCareerPlanId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface WorkerCareerPlanRepository extends Repository<WorkerCareerPlan, WorkerCareerPlanId> {
    CompletionStage<Optional<WorkerCareerPlan>> findActiveByWorker(WorkerId workerId);
}
