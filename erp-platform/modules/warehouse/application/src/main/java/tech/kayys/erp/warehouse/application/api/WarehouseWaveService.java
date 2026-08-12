package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.CreateWaveCommand;
import tech.kayys.erp.warehouse.application.api.command.AddTaskToWaveCommand;
import tech.kayys.erp.warehouse.application.api.command.CompleteWaveTaskCommand;
import tech.kayys.erp.warehouse.application.api.query.WaveView;
import tech.kayys.erp.warehouse.domain.identifier.WaveId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse wave operations.
 */
public interface WarehouseWaveService {

    /**
     * Creates a new wave.
     */
    CompletionStage<WaveId> createWave(CreateWaveCommand command);

    /**
     * Adds a task to a wave.
     */
    CompletionStage<WaveId> addTaskToWave(AddTaskToWaveCommand command);

    /**
     * Plans a wave.
     */
    CompletionStage<WaveId> planWave(WaveId waveId);

    /**
     * Starts a wave.
     */
    CompletionStage<WaveId> startWave(WaveId waveId);

    /**
     * Completes a task in a wave.
     */
    CompletionStage<WaveId> completeWaveTask(CompleteWaveTaskCommand command);

    /**
     * Cancels a wave.
     */
    CompletionStage<WaveId> cancelWave(WaveId waveId, String reason);

    /**
     * Gets a wave by ID.
     */
    CompletionStage<WaveView> getWave(WaveId waveId);

    /**
     * Gets waves by warehouse.
     */
    CompletionStage<List<WaveView>> getWavesByWarehouse(
        UUID warehouseId, String status, String type, int page, int size
    );

    /**
     * Gets active waves (in progress).
     */
    CompletionStage<List<WaveView>> getActiveWaves();

    /**
     * Gets waves by date range.
     */
    CompletionStage<List<WaveView>> getWavesByDateRange(
        Instant start, Instant end
    );

    /**
     * Gets wave performance metrics.
     */
    CompletionStage<WaveMetrics> getWaveMetrics(UUID warehouseId, Instant start, Instant end);
}