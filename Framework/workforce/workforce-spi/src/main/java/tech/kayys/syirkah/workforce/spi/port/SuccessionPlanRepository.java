package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.talent.CriticalPositionId;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionPlan;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionPlanId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SuccessionPlanRepository extends Repository<SuccessionPlan, SuccessionPlanId> {
    CompletionStage<Optional<SuccessionPlan>> findByCriticalPosition(CriticalPositionId criticalPositionId);
    CompletionStage<List<SuccessionPlan>> findActiveByTenant(TenantId tenantId);
}
