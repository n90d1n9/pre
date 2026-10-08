package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.development.CareerPath;
import tech.kayys.syirkah.workforce.domain.development.CareerPathId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CareerPathRepository extends Repository<CareerPath, CareerPathId> {
    CompletionStage<Optional<CareerPath>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<CareerPath>> findActiveByTenant(TenantId tenantId);
}
