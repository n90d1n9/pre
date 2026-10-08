package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceDemand;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceDemandId;
import tech.kayys.syirkah.workforce.domain.position.PositionId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface WorkforceDemandRepository extends Repository<WorkforceDemand, WorkforceDemandId> {
    CompletionStage<List<WorkforceDemand>> findByPosition(PositionId positionId);
    CompletionStage<List<WorkforceDemand>> findActiveByTenant(TenantId tenantId);
}
