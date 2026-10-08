package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.socialinsurance.SocialInsuranceScheme;
import tech.kayys.syirkah.workforce.domain.socialinsurance.SocialInsuranceSchemeId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface SocialInsuranceSchemeRepository extends Repository<SocialInsuranceScheme, SocialInsuranceSchemeId> {

    CompletionStage<Optional<SocialInsuranceScheme>> findByCode(TenantId tenantId, String code);

    CompletionStage<List<SocialInsuranceScheme>> findByTenant(TenantId tenantId);
}
