package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.service.DigitalTwinSimulationService.SimulationResult;
import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse digital twin operations.
 */
public interface WarehouseDigitalTwinService {

    /**
     * Gets the digital twin for a warehouse.
     */
    CompletionStage<WarehouseDigitalTwin> getDigitalTwin(UUID warehouseId);

    /**
     * Gets the layout data for a warehouse.
     */
    CompletionStage<Map<String, Object>> getLayout(UUID warehouseId);

    /**
     * Gets virtual bins for a warehouse.
     */
    CompletionStage<List<WarehouseDigitalTwin.VirtualBin>> getBins(
        UUID warehouseId, String zone, Boolean occupied
    );

    /**
     * Simulates picking optimization.
     */
    CompletionStage<SimulationResult> simulatePicking(
        UUID warehouseId, List<String> productIds, int quantity
    );

    /**
     * Simulates putaway optimization.
     */
    CompletionStage<SimulationResult> simulatePutaway(
        UUID warehouseId, String productId, int quantity, double minVolume
    );

    /**
     * Simulates layout optimization.
     */
    CompletionStage<SimulationResult> simulateLayout(UUID warehouseId);

    /**
     * Simulates inventory forecasting.
     */
    CompletionStage<SimulationResult> simulateForecast(
        UUID warehouseId, String productId, int days
    );

    /**
     * Syncs the digital twin with physical warehouse data.
     */
    CompletionStage<UUID> syncDigitalTwin(UUID warehouseId);

    /**
     * Gets heatmap data for the warehouse.
     */
    CompletionStage<Map<String, Object>> getHeatmap(UUID warehouseId);

    /**
     * Updates the digital twin's accuracy score.
     */
    CompletionStage<UUID> updateAccuracy(UUID warehouseId, double accuracyScore);
}