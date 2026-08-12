package tech.kayys.erp.warehouse.interfaces.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.kayys.erp.warehouse.application.api.WarehouseDigitalTwinService;
import tech.kayys.erp.warehouse.application.service.DigitalTwinSimulationService;
import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket endpoint for warehouse digital twin real-time communication.
 */
@WebSocket(path = "/ws/digital-twin/{warehouseId}")
public class DigitalTwinWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(DigitalTwinWebSocket.class);

    @Inject
    WarehouseDigitalTwinService digitalTwinService;

    @Inject
    DigitalTwinSimulationService simulationService;

    @Inject
    ObjectMapper objectMapper;

    // Store active sessions per warehouse
    private static final Map<String, Map<String, WebSocketConnection>> ACTIVE_SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public CompletionStage<Void> onOpen(WebSocketConnection connection, @PathParam String warehouseId) {
        LOGGER.info("WebSocket opened for warehouse: {}", warehouseId);

        // Add session to active sessions
        ACTIVE_SESSIONS.computeIfAbsent(warehouseId, k -> new ConcurrentHashMap<>())
            .put(connection.id(), connection);

        // Send initial state
        return sendInitialState(connection, warehouseId);
    }

    @OnTextMessage
    public CompletionStage<Void> onMessage(WebSocketConnection connection, String message, @PathParam String warehouseId) {
        LOGGER.debug("WebSocket message received: {}", message);
        
        try {
            Map<String, Object> payload = objectMapper.readValue(message, Map.class);
            String action = (String) payload.get("action");

            return switch (action) {
                case "GET_STATE" -> sendState(connection, warehouseId);
                case "GET_LAYOUT" -> sendLayout(connection, warehouseId);
                case "SYNC" -> handleSync(connection, warehouseId);
                case "SIMULATE_PICKING" -> handleSimulatePicking(connection, warehouseId, payload);
                case "SIMULATE_PUTAWAY" -> handleSimulatePutaway(connection, warehouseId, payload);
                case "SUBSCRIBE_UPDATES" -> handleSubscribe(connection, warehouseId, payload);
                case "UNSUBSCRIBE_UPDATES" -> handleUnsubscribe(connection, warehouseId, payload);
                default -> connection.sendText("{\"error\": \"Unknown action: " + action + "\"}");
            };
        } catch (Exception e) {
            LOGGER.error("Error processing WebSocket message", e);
            return connection.sendText("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @OnClose
    public void onClose(WebSocketConnection connection, @PathParam String warehouseId) {
        LOGGER.info("WebSocket closed for warehouse: {}", warehouseId);
        
        // Remove session from active sessions
        Map<String, WebSocketConnection> warehouseSessions = ACTIVE_SESSIONS.get(warehouseId);
        if (warehouseSessions != null) {
            warehouseSessions.remove(connection.id());
            if (warehouseSessions.isEmpty()) {
                ACTIVE_SESSIONS.remove(warehouseId);
            }
        }
    }

    @OnError
    public void onError(WebSocketConnection connection, Throwable error, @PathParam String warehouseId) {
        LOGGER.error("WebSocket error for warehouse: {}", warehouseId, error);
        connection.close();
    }

    /**
     * Sends the initial state to a new client.
     */
    private CompletionStage<Void> sendInitialState(WebSocketConnection connection, String warehouseId) {
        return sendState(connection, warehouseId)
            .thenCompose(v -> sendLayout(connection, warehouseId));
    }

    /**
     * Sends the current state of the digital twin.
     */
    private CompletionStage<Void> sendState(WebSocketConnection connection, String warehouseId) {
        return digitalTwinService.getDigitalTwin(UUID.fromString(warehouseId))
            .thenAccept(twin -> {
                try {
                    Map<String, Object> response = Map.of(
                        "type", "STATE",
                        "timestamp", Instant.now().toString(),
                        "data", Map.of(
                            "status", twin.getStatus(),
                            "accuracy", twin.getAccuracyScore(),
                            "utilization", twin.getUtilization(),
                            "totalBins", twin.getVirtualBinCount(),
                            "lastSync", twin.getLastSyncTime() != null ? 
                                twin.getLastSyncTime().toString() : null
                        )
                    );
                    connection.sendText(objectMapper.writeValueAsString(response));
                } catch (Exception e) {
                    LOGGER.error("Error sending state", e);
                }
            });
    }

    /**
     * Sends the layout data.
     */
    private CompletionStage<Void> sendLayout(WebSocketConnection connection, String warehouseId) {
        return digitalTwinService.getLayout(UUID.fromString(warehouseId))
            .thenAccept(layout -> {
                try {
                    Map<String, Object> response = Map.of(
                        "type", "LAYOUT",
                        "timestamp", Instant.now().toString(),
                        "data", layout
                    );
                    connection.sendText(objectMapper.writeValueAsString(response));
                } catch (Exception e) {
                    LOGGER.error("Error sending layout", e);
                }
            });
    }

    /**
     * Handles sync request.
     */
    private CompletionStage<Void> handleSync(WebSocketConnection connection, String warehouseId) {
        return digitalTwinService.syncDigitalTwin(UUID.fromString(warehouseId))
            .thenAccept(result -> {
                try {
                    Map<String, Object> response = Map.of(
                        "type", "SYNC_RESULT",
                        "timestamp", Instant.now().toString(),
                        "data", Map.of(
                            "success", true,
                            "message", "Sync completed",
                            "warehouseId", warehouseId
                        )
                    );
                    connection.sendText(objectMapper.writeValueAsString(response));
                    
                    // Broadcast updated state to all subscribers
                    broadcastStateUpdate(warehouseId);
                } catch (Exception e) {
                    LOGGER.error("Error handling sync", e);
                }
            });
    }

    /**
     * Handles simulate picking request.
     */
    private CompletionStage<Void> handleSimulatePicking(
            WebSocketConnection connection, 
            String warehouseId, 
            Map<String, Object> payload) {
        
        @SuppressWarnings("unchecked")
        List<String> productIds = (List<String>) payload.get("productIds");
        int quantity = (int) payload.getOrDefault("quantity", 1);

        return digitalTwinService.simulatePicking(UUID.fromString(warehouseId), productIds, quantity)
            .thenAccept(result -> {
                try {
                    Map<String, Object> response = Map.of(
                        "type", "SIMULATION_RESULT",
                        "simulationType", "PICKING",
                        "timestamp", Instant.now().toString(),
                        "data", Map.of(
                            "status", result.status(),
                            "durationMs", result.durationMs(),
                            "itemsProcessed", result.itemsProcessed(),
                            "distanceOptimized", result.distanceOptimized(),
                            "summary", result.summary()
                        )
                    );
                    connection.sendText(objectMapper.writeValueAsString(response));
                } catch (Exception e) {
                    LOGGER.error("Error handling simulation", e);
                }
            });
    }

    /**
     * Handles simulate putaway request.
     */
    private CompletionStage<Void> handleSimulatePutaway(
            WebSocketConnection connection,
            String warehouseId,
            Map<String, Object> payload) {

        String productId = (String) payload.get("productId");
        int quantity = (int) payload.getOrDefault("quantity", 1);
        double minVolume = (double) payload.getOrDefault("minVolume", 0.0);

        return digitalTwinService.simulatePutaway(
                UUID.fromString(warehouseId), productId, quantity, minVolume)
            .thenAccept(result -> {
                try {
                    Map<String, Object> response = Map.of(
                        "type", "SIMULATION_RESULT",
                        "simulationType", "PUTAWAY",
                        "timestamp", Instant.now().toString(),
                        "data", Map.of(
                            "status", result.status(),
                            "durationMs", result.durationMs(),
                            "itemsProcessed", result.itemsProcessed(),
                            "summary", result.summary()
                        )
                    );
                    connection.sendText(objectMapper.writeValueAsString(response));
                } catch (Exception e) {
                    LOGGER.error("Error handling putaway simulation", e);
                }
            });
    }

    /**
     * Handles subscription to updates.
     */
    private CompletionStage<Void> handleSubscribe(
            WebSocketConnection connection,
            String warehouseId,
            Map<String, Object> payload) {

        String subscriptionType = (String) payload.getOrDefault("subscriptionType", "ALL");
        LOGGER.info("Client subscribed to {} updates for warehouse {}", subscriptionType, warehouseId);

        // Store subscription preference
        connection.setAttribute("subscriptionType", subscriptionType);
        
        return connection.sendText("{\"type\":\"SUBSCRIBE_SUCCESS\",\"subscriptionType\":\"" + subscriptionType + "\"}");
    }

    /**
     * Handles unsubscribe.
     */
    private CompletionStage<Void> handleUnsubscribe(
            WebSocketConnection connection,
            String warehouseId,
            Map<String, Object> payload) {

        connection.removeAttribute("subscriptionType");
        return connection.sendText("{\"type\":\"UNSUBSCRIBE_SUCCESS\"}");
    }

    /**
     * Broadcasts a state update to all subscribers of a warehouse.
     */
    public static void broadcastStateUpdate(String warehouseId) {
        Map<String, WebSocketConnection> sessions = ACTIVE_SESSIONS.get(warehouseId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        Map<String, Object> update = Map.of(
            "type", "STATE_UPDATE",
            "timestamp", Instant.now().toString()
        );

        sessions.values().forEach(conn -> {
            try {
                conn.sendText(new ObjectMapper().writeValueAsString(update));
            } catch (Exception e) {
                LOGGER.error("Error broadcasting state update", e);
            }
        });
    }

    /**
     * Broadcasts a bin update to all subscribers.
     */
    public static void broadcastBinUpdate(String warehouseId, Map<String, Object> binData) {
        Map<String, WebSocketConnection> sessions = ACTIVE_SESSIONS.get(warehouseId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        Map<String, Object> update = Map.of(
            "type", "BIN_UPDATE",
            "timestamp", Instant.now().toString(),
            "data", binData
        );

        sessions.values().forEach(conn -> {
            try {
                conn.sendText(new ObjectMapper().writeValueAsString(update));
            } catch (Exception e) {
                LOGGER.error("Error broadcasting bin update", e);
            }
        });
    }

    /**
     * Broadcasts a simulation progress update.
     */
    public static void broadcastSimulationProgress(
            String warehouseId, 
            String simulationId, 
            double progress, 
            String status) {
        
        Map<String, WebSocketConnection> sessions = ACTIVE_SESSIONS.get(warehouseId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }

        Map<String, Object> update = Map.of(
            "type", "SIMULATION_PROGRESS",
            "timestamp", Instant.now().toString(),
            "data", Map.of(
                "simulationId", simulationId,
                "progress", progress,
                "status", status
            )
        );

        sessions.values().forEach(conn -> {
            try {
                conn.sendText(new ObjectMapper().writeValueAsString(update));
            } catch (Exception e) {
                LOGGER.error("Error broadcasting simulation progress", e);
            }
        });
    }
}