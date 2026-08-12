package tech.kayys.erp.warehouse.infrastructure.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.kayys.erp.warehouse.application.api.WarehouseDigitalTwinService;
import tech.kayys.erp.warehouse.application.service.DigitalTwinSimulationService;
import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin;

import javax.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * gRPC service implementation for warehouse digital twin.
 */
@GrpcService
public class DigitalTwinGrpcService extends DigitalTwinServiceGrpc.DigitalTwinServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(DigitalTwinGrpcService.class);

    @Inject
    WarehouseDigitalTwinService digitalTwinService;

    @Inject
    DigitalTwinSimulationService simulationService;

    @Inject
    ObjectMapper objectMapper;

    // Active sensor streams
    private static final Map<String, StreamObserver<SensorDataResponse>> ACTIVE_SENSOR_STREAMS = new ConcurrentHashMap<>();

    @Override
    public void getState(StateRequest request, StreamObserver<StateResponse> responseObserver) {
        LOGGER.info("gRPC getState called for warehouse: {}", request.getWarehouseId());
        
        try {
            WarehouseDigitalTwin twin = digitalTwinService.getDigitalTwin(
                UUID.fromString(request.getWarehouseId())
            ).toCompletableFuture().join();

            StateResponse response = StateResponse.newBuilder()
                .setWarehouseId(request.getWarehouseId())
                .setStatus(twin.getStatus())
                .setAccuracyScore(twin.getAccuracyScore())
                .setUtilization(twin.getUtilization())
                .setTotalBins(twin.getVirtualBinCount())
                .setLastSyncTime(twin.getLastSyncTime() != null ? 
                    twin.getLastSyncTime().toString() : "")
                .setTimestamp(Instant.now().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in getState", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getLayout(LayoutRequest request, StreamObserver<LayoutResponse> responseObserver) {
        LOGGER.info("gRPC getLayout called for warehouse: {}", request.getWarehouseId());
        
        try {
            Map<String, Object> layout = digitalTwinService.getLayout(
                UUID.fromString(request.getWarehouseId())
            ).toCompletableFuture().join();

            String layoutJson = objectMapper.writeValueAsString(layout);

            LayoutResponse response = LayoutResponse.newBuilder()
                .setWarehouseId(request.getWarehouseId())
                .setLayoutData(layoutJson)
                .setTimestamp(Instant.now().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in getLayout", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getBins(BinsRequest request, StreamObserver<BinsResponse> responseObserver) {
        LOGGER.info("gRPC getBins called for warehouse: {}", request.getWarehouseId());
        
        try {
            List<WarehouseDigitalTwin.VirtualBin> bins = digitalTwinService.getBins(
                UUID.fromString(request.getWarehouseId()),
                request.getZone().isEmpty() ? null : request.getZone(),
                request.hasOccupied() ? request.getOccupied() : null
            ).toCompletableFuture().join();

            BinsResponse.Builder responseBuilder = BinsResponse.newBuilder()
                .setWarehouseId(request.getWarehouseId())
                .setTotalCount(bins.size());

            for (WarehouseDigitalTwin.VirtualBin bin : bins) {
                responseBuilder.addBins(VirtualBin.newBuilder()
                    .setId(bin.getId())
                    .setCode(bin.getCode() != null ? bin.getCode() : "")
                    .setZone(bin.getZone() != null ? bin.getZone() : "")
                    .setAisle(bin.getAisle() != null ? bin.getAisle() : "")
                    .setLevel(bin.getLevel() != null ? bin.getLevel() : "")
                    .setPosition(bin.getPosition() != null ? bin.getPosition() : "")
                    .setXCoordinate(bin.getXCoordinate())
                    .setYCoordinate(bin.getYCoordinate())
                    .setZCoordinate(bin.getZCoordinate())
                    .setCapacity(bin.getCapacity())
                    .setOccupied(bin.getOccupied())
                    .setProductId(bin.getProductId() != null ? bin.getProductId() : "")
                    .setProductName(bin.getProductName() != null ? bin.getProductName() : "")
                    .setIsOccupied(bin.isOccupied())
                    .build()
                );
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in getBins", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void streamUpdates(StreamRequest request, StreamObserver<StreamResponse> responseObserver) {
        LOGGER.info("gRPC streamUpdates called for warehouse: {}", request.getWarehouseId());
        
        // Store the observer for later use
        // In a real implementation, we'd use a registry to send updates
        
        try {
            // Send initial state
            WarehouseDigitalTwin twin = digitalTwinService.getDigitalTwin(
                UUID.fromString(request.getWarehouseId())
            ).toCompletableFuture().join();

            Map<String, Object> initialState = Map.of(
                "status", twin.getStatus(),
                "accuracy", twin.getAccuracyScore(),
                "utilization", twin.getUtilization(),
                "totalBins", twin.getVirtualBinCount()
            );

            String data = objectMapper.writeValueAsString(initialState);

            StreamResponse response = StreamResponse.newBuilder()
                .setType("STATE_UPDATE")
                .setTimestamp(Instant.now().toString())
                .setData(data)
                .build();

            responseObserver.onNext(response);
            
            // Keep the stream open for future updates
            // In a real implementation, we'd wait for updates and send them
            
            // For now, send a heartbeat every 30 seconds
            // In production, use proper push notifications
            while (true) {
                Thread.sleep(30000);
                Map<String, Object> heartbeat = Map.of("type", "HEARTBEAT");
                String heartbeatData = objectMapper.writeValueAsString(heartbeat);
                
                StreamResponse heartbeatResponse = StreamResponse.newBuilder()
                    .setType("HEARTBEAT")
                    .setTimestamp(Instant.now().toString())
                    .setData(heartbeatData)
                    .build();
                
                responseObserver.onNext(heartbeatResponse);
            }
            
        } catch (Exception e) {
            LOGGER.error("Error in streamUpdates", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void sync(SyncRequest request, StreamObserver<SyncResponse> responseObserver) {
        LOGGER.info("gRPC sync called for warehouse: {}", request.getWarehouseId());
        
        try {
            UUID warehouseId = UUID.fromString(request.getWarehouseId());
            UUID result = digitalTwinService.syncDigitalTwin(warehouseId)
                .toCompletableFuture().join();

            SyncResponse response = SyncResponse.newBuilder()
                .setWarehouseId(request.getWarehouseId())
                .setSuccess(result != null)
                .setMessage(result != null ? "Sync completed successfully" : "Sync failed")
                .setTimestamp(Instant.now().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in sync", e);
            SyncResponse response = SyncResponse.newBuilder()
                .setWarehouseId(request.getWarehouseId())
                .setSuccess(false)
                .setMessage("Sync failed: " + e.getMessage())
                .setTimestamp(Instant.now().toString())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void simulatePicking(SimulatePickingRequest request, StreamObserver<SimulationResponse> responseObserver) {
        LOGGER.info("gRPC simulatePicking called for warehouse: {}", request.getWarehouseId());
        
        try {
            List<String> productIds = request.getProductIdsList();
            int quantity = request.getQuantity();

            DigitalTwinSimulationService.SimulationResult result = 
                digitalTwinService.simulatePicking(
                    UUID.fromString(request.getWarehouseId()),
                    productIds,
                    quantity
                ).toCompletableFuture().join();

            SimulationResponse response = SimulationResponse.newBuilder()
                .setSimulationType(result.simulationType())
                .setStatus(result.status())
                .setDurationMs(result.durationMs())
                .setItemsProcessed(result.itemsProcessed())
                .setDistanceOptimized(result.distanceOptimized())
                .setSummary(result.summary())
                .setTimestamp(result.timestamp().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in simulatePicking", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void simulatePutaway(SimulatePutawayRequest request, StreamObserver<SimulationResponse> responseObserver) {
        LOGGER.info("gRPC simulatePutaway called for warehouse: {}", request.getWarehouseId());
        
        try {
            DigitalTwinSimulationService.SimulationResult result = 
                digitalTwinService.simulatePutaway(
                    UUID.fromString(request.getWarehouseId()),
                    request.getProductId(),
                    request.getQuantity(),
                    request.getMinVolume()
                ).toCompletableFuture().join();

            SimulationResponse response = SimulationResponse.newBuilder()
                .setSimulationType(result.simulationType())
                .setStatus(result.status())
                .setDurationMs(result.durationMs())
                .setItemsProcessed(result.itemsProcessed())
                .setDistanceOptimized(result.distanceOptimized())
                .setSummary(result.summary())
                .setTimestamp(result.timestamp().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in simulatePutaway", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void simulateLayout(SimulateLayoutRequest request, StreamObserver<SimulationResponse> responseObserver) {
        LOGGER.info("gRPC simulateLayout called for warehouse: {}", request.getWarehouseId());
        
        try {
            DigitalTwinSimulationService.SimulationResult result = 
                digitalTwinService.simulateLayout(
                    UUID.fromString(request.getWarehouseId())
                ).toCompletableFuture().join();

            SimulationResponse response = SimulationResponse.newBuilder()
                .setSimulationType(result.simulationType())
                .setStatus(result.status())
                .setDurationMs(result.durationMs())
                .setItemsProcessed(result.itemsProcessed())
                .setDistanceOptimized(result.distanceOptimized())
                .setSummary(result.summary())
                .setTimestamp(result.timestamp().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in simulateLayout", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getHeatmap(HeatmapRequest request, StreamObserver<HeatmapResponse> responseObserver) {
        LOGGER.info("gRPC getHeatmap called for warehouse: {}", request.getWarehouseId());
        
        try {
            Map<String, Object> heatmap = digitalTwinService.getHeatmap(
                UUID.fromString(request.getWarehouseId())
            ).toCompletableFuture().join();

            String heatmapJson = objectMapper.writeValueAsString(heatmap);

            HeatmapResponse response = HeatmapResponse.newBuilder()
                .setWarehouseId(request.getWarehouseId())
                .setHeatmapData(heatmapJson)
                .setTimestamp(Instant.now().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.error("Error in getHeatmap", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public StreamObserver<SensorDataRequest> streamSensorData(
            StreamObserver<SensorDataResponse> responseObserver) {
        
        return new StreamObserver<>() {
            @Override
            public void onNext(SensorDataRequest request) {
                LOGGER.debug("Sensor data received: {}", request);
                
                try {
                    // Process sensor data
                    SensorDataResponse response = SensorDataResponse.newBuilder()
                        .setWarehouseId(request.getWarehouseId())
                        .setSensorId(request.getSensorId())
                        .setSensorType(request.getSensorType())
                        .setValue(request.getValue())
                        .setTimestamp(request.getTimestamp())
                        .setProcessed(true)
                        .build();

                    // Forward to all subscribers for this warehouse
                    StreamObserver<SensorDataResponse> subscriber = 
                        ACTIVE_SENSOR_STREAMS.get(request.getWarehouseId());
                    if (subscriber != null) {
                        subscriber.onNext(response);
                    }

                    responseObserver.onNext(response);
                } catch (Exception e) {
                    LOGGER.error("Error processing sensor data", e);
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                LOGGER.error("Sensor stream error", t);
                // Remove from active streams
                ACTIVE_SENSOR_STREAMS.values().remove(responseObserver);
            }

            @Override
            public void onCompleted() {
                LOGGER.info("Sensor stream completed");
                // Remove from active streams
                ACTIVE_SENSOR_STREAMS.values().remove(responseObserver);
                responseObserver.onCompleted();
            }
        };
    }
}