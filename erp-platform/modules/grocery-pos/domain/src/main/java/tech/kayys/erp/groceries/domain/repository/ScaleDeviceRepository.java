package tech.kayys.erp.groceries.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.groceries.domain.identifier.ScaleId;
import tech.kayys.erp.groceries.domain.model.ScaleDevice;

import java.util.concurrent.CompletionStage;

/**
 * Repository for ScaleDevice aggregate.
 */
public interface ScaleDeviceRepository extends Repository<ScaleId, ScaleDevice> {

    CompletionStage<ScaleDevice> findBySerialNumber(String serialNumber);

    CompletionStage<java.util.List<ScaleDevice>> findByScaleType(ScaleDevice.ScaleType scaleType);

    CompletionStage<java.util.List<ScaleDevice>> findConnectedScales();
}
