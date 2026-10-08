package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.talent.TalentSegment;
import tech.kayys.syirkah.workforce.domain.talent.TalentSegmentId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface TalentSegmentRepository extends Repository<TalentSegment, TalentSegmentId> {
    CompletionStage<Optional<TalentSegment>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<TalentSegment>> findActiveByTenant(TenantId tenantId);
}
