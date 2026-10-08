package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentActivity;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentActivityId;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentNeedId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DevelopmentActivityRepository extends Repository<DevelopmentActivity, DevelopmentActivityId> {
    CompletionStage<List<DevelopmentActivity>> findByNeed(DevelopmentNeedId needId);
    CompletionStage<List<DevelopmentActivity>> findByWorker(WorkerId workerId);
}
