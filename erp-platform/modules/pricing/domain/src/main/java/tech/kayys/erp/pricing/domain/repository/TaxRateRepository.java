package tech.kayys.erp.pricing.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.pricing.domain.identifier.TaxRateId;
import tech.kayys.erp.pricing.domain.valueobject.TaxRate;
import tech.kayys.erp.pricing.domain.valueobject.TaxType;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for TaxRate aggregates.
 */
public interface TaxRateRepository extends Repository<TaxRate, TaxRateId> {

    /**
     * Finds tax rates by jurisdiction.
     */
    CompletionStage<List<TaxRate>> findByJurisdiction(String jurisdiction);

    /**
     * Finds tax rates by type.
     */
    CompletionStage<List<TaxRate>> findByType(TaxType type);

    /**
     * Finds effective tax rates at a given time.
     */
    CompletionStage<List<TaxRate>> findEffectiveAt(Instant time);

    /**
     * Finds currently effective tax rates.
     */
    default CompletionStage<List<TaxRate>> findCurrentEffective() {
        return findEffectiveAt(Instant.now());
    }

    /**
     * Finds tax rates by jurisdiction and type.
     */
    CompletionStage<List<TaxRate>> findByJurisdictionAndType(
        String jurisdiction, 
        TaxType type
    );
}