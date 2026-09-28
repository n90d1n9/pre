package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.paycomponent.PayComponent;
import tech.kayys.syirkah.workforce.domain.paycomponent.PayComponentId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PayComponentRepository extends Repository<PayComponent, PayComponentId> {

    CompletionStage<Optional<PayComponent>> findByCode(TenantId tenantId, String code);

    CompletionStage<List<PayComponent>> findByTenant(TenantId tenantId);
}
