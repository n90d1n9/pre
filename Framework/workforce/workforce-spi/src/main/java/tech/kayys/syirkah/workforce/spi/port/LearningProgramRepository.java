package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.learning.LearningProgram;
import tech.kayys.syirkah.workforce.domain.learning.LearningProgramId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface LearningProgramRepository extends Repository<LearningProgram, LearningProgramId> {
    CompletionStage<Optional<LearningProgram>> findByTenantAndCode(TenantId tenantId, String code);
    CompletionStage<List<LearningProgram>> findActiveByTenant(TenantId tenantId);
}
