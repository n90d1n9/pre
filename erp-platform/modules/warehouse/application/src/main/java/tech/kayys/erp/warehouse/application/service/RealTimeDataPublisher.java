package tech.kayys.erp.warehouse.application.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for publishing real-time warehouse data.
 */
@ApplicationScoped
public class RealTimeDataPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealTimeDataPublisher.class);

    @Inject
    @Channel("warehouse-events")
    Emitter<String> eventEmitter;

    @Inject
    @Channel("sensor-data")
    Emitter<String> sensorEmitter;

    @Inject
    @Channel("simulation-events")
    Emitter<String> simulationEmitter;

    private final Map<String, Long> lastUpdateTimes = new ConcurrentHashMap<>();

    /**
     * Publishes a warehouse state update.
     */
    public void publishStateUpdate(UUID warehouseId, WarehouseDigitalTwin twin) {
        try {
            Map<String, Object> event = Map.of(
                "type", "STATE_UPDATE",
                "warehouseId", warehouseId.toString(),
                "timestamp", Instant.now().toString(),
                "data", Map.of(
                    "status", twin.getStatus(),
                    "accuracy", twin.getAccuracyScore(),
                    "utilization", twin.getUtilization(),
                    "totalBins", twin.getVirtualBinCount()
                )
            );

            eventEmitter.send(convertToJson(event));
            LOGGER.debug("State update published for warehouse: {}", warehouseId);
        } catch (Exception e) {
            LOGGER.error("Error publishing state update", e);
        }
    }

    /**
     * Publishes a bin update.
     */
    public void publishBinUpdate(UUID warehouseId, WarehouseDigitalTwin.VirtualBin bin) {
        try {
            // Rate limit to prevent spam
            String key = warehouseId + ":" + bin.getId();
            Long lastUpdate = lastUpdateTimes.get(key);
            if (lastUpdate != null && Instant.now().toEpochMilli() - lastUpdate < 1000) {
                return; // Skip if updated within last second
            }
            lastUpdateTimes.put(key, Instant.now().toEpochMilli());

            Map<String, Object> event = Map.of(
                "type", "BIN_UPDATE",
                "warehouseId", warehouseId.toString(),
                "timestamp", Instant.now().toString(),
                "data", Map.of(
                    "binId", bin.getId(),
                    "code", bin.getCode(),
                    "occupancy", bin.getOccupied(),
                    "utilization", bin.getUtilization(),
                    "productId", bin.getProductId(),
                    "isOccupied", bin.isOccupied()
                )
            );

            eventEmitter.send(convertToJson(event));
        } catch (Exception e) {
            LOGGER.error("Error publishing bin update", e);
        }
    }

    /**
     * Publishes sensor data.
     */
    public void publishSensorData(UUID warehouseId, String sensorId, String sensorType, double value) {
        try {
            Map<String, Object> event = Map.of(
                "type", "SENSOR_DATA",
                "warehouseId", warehouseId.toString(),
                "sensorId", sensorId,
                "sensorType", sensorType,
                "value", value,
                "timestamp", Instant.now().toString()
            );

            sensorEmitter.send(convertToJson(event));
        } catch (Exception e) {
            LOGGER.error("Error publishing sensor data", e);
        }
    }

    /**
     * Publishes simulation progress.
     */
    public void publishSimulationProgress(
            UUID warehouseId, 
            String simulationId, 
            double progress, 
            String status) {
        
        try {
            Map<String, Object> event = Map.of(
                "type", "SIMULATION_PROGRESS",
                "warehouseId", warehouseId.toString(),
                "simulationId", simulationId,
                "progress", progress,
                "status", status,
                "timestamp", Instant.now().toString()
            );

            simulationEmitter.send(convertToJson(event));
        } catch (Exception e) {
            LOGGER.error("Error publishing simulation progress", e);
        }
    }

    /**
     * Publishes a simulation result.
     */
    public void publishSimulationResult(
            UUID warehouseId,
            String simulationType,
            DigitalTwinSimulationService.SimulationResult result) {
        
        try {
            Map<String, Object> event = Map.of(
                "type", "SIMULATION_COMPLETE",
                "warehouseId", warehouseId.toString(),
                "simulationType", simulationType,
                "timestamp", Instant.now().toString(),
                "data", Map.of(
                    "status", result.status(),
                    "durationMs", result.durationMs(),
                    "summary", result.summary()
                )
            );

            simulationEmitter.send(convertToJson(event));
        } catch (Exception e) {
            LOGGER.error("Error publishing simulation result", e);
        }
    }

    @Incoming("warehouse-events")
    public Uni<Void> consumeWarehouseEvent(String event) {
        LOGGER.debug("Received warehouse event: {}", event);
        return Uni.createFrom().voidItem();
    }

    @Incoming("sensor-data")
    public Uni<Void> consumeSensorData(String data) {
        LOGGER.debug("Received sensor data: {}", data);
        return Uni.createFrom().voidItem();
    }

    @Incoming("simulation-events")
    public Uni<Void> consumeSimulationEvent(String event) {
        LOGGER.debug("Received simulation event: {}", event);
        return Uni.createFrom().voidItem();
    }

    /**
     * Converts a map to JSON string.
     */
    private String convertToJson(Map<String, Object> map) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(map);
        } catch (Exception e) {
            LOGGER.error("Error converting to JSON", e);
            return "{}";
        }
    }

    /**
     * Creates a reactive stream of warehouse events.
     */
    public Multi<String> streamWarehouseEvents(UUID warehouseId) {
        // In production, use a proper reactive stream
        return Multi.createFrom().items(
            "{\"type\":\"SUBSCRIBED\",\"warehouseId\":\"" + warehouseId + "\"}"
        );
    }

    /**
     * Creates a reactive stream of sensor data.
     */
    public Multi<String> streamSensorData(UUID warehouseId) {
        // In production, use a proper reactive stream
        return Multi.createFrom().items(
            "{\"type\":\"SENSOR_STREAM_STARTED\",\"warehouseId\":\"" + warehouseId + "\"}"
        );
    }
}