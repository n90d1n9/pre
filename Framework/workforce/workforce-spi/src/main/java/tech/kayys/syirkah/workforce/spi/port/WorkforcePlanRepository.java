package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforcePlan;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforcePlanId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface WorkforcePlanRepository extends Repository<WorkforcePlan, WorkforcePlanId> {
    CompletionStage<Optional<WorkforcePlan>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<WorkforcePlan>> findByTenant(TenantId tenantId);
}
