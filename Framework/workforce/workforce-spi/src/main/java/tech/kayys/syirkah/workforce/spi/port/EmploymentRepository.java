package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.employment.Employment;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.employment.OrganizationRef;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Persistence port for the {@link Employment} aggregate root.
 */
public interface EmploymentRepository extends Repository<Employment, EmploymentId> {

    /**
     * Finds all employments associated with a specific worker.
     */
    CompletionStage<List<Employment>> findByWorkerId(WorkerId workerId);

    /**
     * Finds all employments associated with an organization.
     */
    CompletionStage<List<Employment>> findByOrganizationRef(OrganizationRef organizationRef);
}
