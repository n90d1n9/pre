package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.talent.TalentReviewCycle;
import tech.kayys.syirkah.workforce.domain.talent.TalentReviewCycleId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TalentReviewCycleRepository extends Repository<TalentReviewCycle, TalentReviewCycleId> {
    CompletionStage<Optional<TalentReviewCycle>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<TalentReviewCycle>> findActiveByTenant(TenantId tenantId);
}
