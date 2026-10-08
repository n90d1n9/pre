package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.benefit.Benefit;
import tech.kayys.syirkah.workforce.domain.benefit.BenefitId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface BenefitRepository extends Repository<Benefit, BenefitId> {

    CompletionStage<Optional<Benefit>> findByCode(TenantId tenantId, String code);

    CompletionStage<List<Benefit>> findByTenant(TenantId tenantId);
}
