package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionPool;
import tech.kayys.syirkah.workforce.domain.talent.SuccessionPoolId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SuccessionPoolRepository extends Repository<SuccessionPool, SuccessionPoolId> {
    CompletionStage<Optional<SuccessionPool>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<SuccessionPool>> findActiveByTenant(TenantId tenantId);
}
