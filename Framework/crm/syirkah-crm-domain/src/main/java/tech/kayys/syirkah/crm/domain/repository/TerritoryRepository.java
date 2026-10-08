package tech.kayys.syirkah.crm.domain.repository;

import tech.kayys.syirkah.crm.domain.territory.Territory;
import tech.kayys.syirkah.crm.domain.territory.TerritoryId;
import tech.kayys.syirkah.foundation.domain.repository.Repository;

/**
 * Repository port for {@link Territory} aggregates.
 */
public interface TerritoryRepository extends Repository<Territory, TerritoryId> {
}