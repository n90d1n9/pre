package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceMetricDefinition;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceMetricDefinitionId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface WorkforceMetricDefinitionRepository extends Repository<WorkforceMetricDefinition, WorkforceMetricDefinitionId> {
    CompletionStage<Optional<WorkforceMetricDefinition>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<WorkforceMetricDefinition>> findActiveByTenant(TenantId tenantId);
}
