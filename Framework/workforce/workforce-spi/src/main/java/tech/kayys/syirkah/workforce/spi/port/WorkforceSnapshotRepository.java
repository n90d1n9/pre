package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceSnapshot;
import tech.kayys.syirkah.workforce.domain.analytics.WorkforceSnapshotId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface WorkforceSnapshotRepository extends Repository<WorkforceSnapshot, WorkforceSnapshotId> {
    CompletionStage<Optional<WorkforceSnapshot>> findByDateAndScope(TenantId tenantId, LocalDate date, String scope);
    CompletionStage<List<WorkforceSnapshot>> findByTenant(TenantId tenantId);
}
