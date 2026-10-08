package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentNeed;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentNeedId;
import tech.kayys.syirkah.workforce.domain.development.DevelopmentPlanId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface DevelopmentNeedRepository extends Repository<DevelopmentNeed, DevelopmentNeedId> {
    CompletionStage<List<DevelopmentNeed>> findByPlan(DevelopmentPlanId planId);
}
