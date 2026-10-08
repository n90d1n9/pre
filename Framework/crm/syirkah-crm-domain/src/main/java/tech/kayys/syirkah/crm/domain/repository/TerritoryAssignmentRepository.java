package tech.kayys.syirkah.crm.domain.repository;

import tech.kayys.syirkah.crm.domain.territory.TerritoryAssignment;
import tech.kayys.syirkah.crm.domain.territory.TerritoryAssignmentId;
import tech.kayys.syirkah.foundation.domain.repository.Repository;

/**
 * Repository port for {@link TerritoryAssignment} aggregates.
 */
public interface TerritoryAssignmentRepository extends Repository<TerritoryAssignment, TerritoryAssignmentId> {
}