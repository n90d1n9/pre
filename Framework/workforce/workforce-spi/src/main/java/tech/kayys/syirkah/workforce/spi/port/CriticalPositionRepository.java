package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.position.PositionId;
import tech.kayys.syirkah.workforce.domain.talent.CriticalPosition;
import tech.kayys.syirkah.workforce.domain.talent.CriticalPositionId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CriticalPositionRepository extends Repository<CriticalPosition, CriticalPositionId> {
    CompletionStage<Optional<CriticalPosition>> findByPosition(PositionId positionId);
    CompletionStage<List<CriticalPosition>> findActiveByTenant(TenantId tenantId);
}
