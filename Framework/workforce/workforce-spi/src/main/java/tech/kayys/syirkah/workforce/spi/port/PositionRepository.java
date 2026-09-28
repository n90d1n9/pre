package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.employment.OrganizationRef;
import tech.kayys.syirkah.workforce.domain.position.Position;
import tech.kayys.syirkah.workforce.domain.position.PositionId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Persistence port for the {@link Position} aggregate root.
 */
public interface PositionRepository extends Repository<Position, PositionId> {

    /**
     * Finds all positions in an organization.
     */
    CompletionStage<List<Position>> findByOrganizationRef(OrganizationRef organizationRef);
}
