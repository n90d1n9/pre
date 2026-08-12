package tech.kayys.erp.warehouse.interfaces.rest;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.service.RealTimeDataPublisher;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for real-time warehouse dashboard.
 */
@Path("/api/v1/warehouses/{warehouseId}/realtime")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Real-Time Dashboard", description = "Real-time warehouse monitoring endpoints")
public class RealTimeDashboardResource {

    @Inject
    RealTimeDataPublisher dataPublisher;

    @GET
    @Path("/events")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Operation(summary = "Stream warehouse events (SSE)")
    public Multi<String> streamEvents(@PathParam("warehouseId") UUID warehouseId) {
        return dataPublisher.streamWarehouseEvents(warehouseId);
    }

    @GET
    @Path("/sensors")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Operation(summary = "Stream sensor data (SSE)")
    public Multi<String> streamSensors(@PathParam("warehouseId") UUID warehouseId) {
        return dataPublisher.streamSensorData(warehouseId);
    }

    @POST
    @Path("/sensors/{sensorId}/data")
    @Operation(summary = "Publish sensor data")
    public CompletionStage<Response> publishSensorData(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("sensorId") String sensorId,
            SensorDataRequest request) {
        
        dataPublisher.publishSensorData(
            warehouseId,
            sensorId,
            request.getSensorType(),
            request.getValue()
        );

        return CompletableFuture.completedFuture(
            Response.ok(Map.of(
                "success", true,
                "message", "Sensor data published",
                "timestamp", Instant.now().toString()
            )).build()
        );
    }

    @GET
    @Path("/heatmap")
    @Operation(summary = "Get real-time heatmap data")
    public CompletionStage<Response> getHeatmap(@PathParam("warehouseId") UUID warehouseId) {
        // Return real-time heatmap data
        // In production, this would be generated from the digital twin
        Map<String, Object> heatmap = Map.of(
            "warehouseId", warehouseId.toString(),
            "timestamp", Instant.now().toString(),
            "data", generateHeatmapData(warehouseId)
        );
        return CompletableFuture.completedFuture(Response.ok(heatmap).build());
    }

    @GET
    @Path("/metrics")
    @Operation(summary = "Get real-time metrics")
    public CompletionStage<Response> getMetrics(@PathParam("warehouseId") UUID warehouseId) {
        Map<String, Object> metrics = Map.of(
            "warehouseId", warehouseId.toString(),
            "timestamp", Instant.now().toString(),
            "metrics", Map.of(
                "utilization", "85.5%",
                "throughput", "156 items/hour",
                "avgPickTime", "2.3 minutes",
                "accuracy", "99.8%",
                "activePickers", 12,
                "pendingOrders", 45
            )
        );
        return CompletableFuture.completedFuture(Response.ok(metrics).build());
    }

    /**
     * Generates heatmap data.
     */
    private Map<String, Object> generateHeatmapData(UUID warehouseId) {
        // Simulated heatmap data
        return Map.of(
            "zones", Map.of(
                "Zone A", Map.of("utilization", 92.5, "color", "#ff0000"),
                "Zone B", Map.of("utilization", 68.3, "color", "#ff8800"),
                "Zone C", Map.of("utilization", 45.7, "color", "#88ff00"),
                "Zone D", Map.of("utilization", 78.2, "color", "#ff4400"),
                "Zone E", Map.of("utilization", 23.1, "color", "#44ff00")
            ),
            "hotspots", Map.of(
                "aisle-12", Map.of("intensity", 95.0, "items", 342),
                "aisle-7", Map.of("intensity", 88.0, "items", 298),
                "aisle-3", Map.of("intensity", 72.0, "items", 245)
            )
        );
    }

    /**
     * Sensor data request DTO.
     */
    public static class SensorDataRequest {
        private String sensorType;
        private double value;

        public String getSensorType() { return sensorType; }
        public void setSensorType(String sensorType) { this.sensorType = sensorType; }
        public double getValue() { return value; }
        public void setValue(double value) { this.value = value; }
    }
}
<!-- Add to modules/warehouse/pom.xml -->
<dependencies>
    <!-- Existing dependencies -->
    
    <!-- WebSocket -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-websockets-next</artifactId>
    </dependency>
    
    <!-- gRPC -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-grpc</artifactId>
    </dependency>
    
    <!-- Reactive Messaging -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-smallrye-reactive-messaging</artifactId>
    </dependency>
    
    <!-- SSE -->
    <dependency>
        <groupId>io.quarkus</groupId>
        <artifactId>quarkus-resteasy-reactive</artifactId>
    </dependency>
</dependencies>