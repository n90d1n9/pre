package tech.kayys.erp.warehouse.application.service;

import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin;
import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin.VirtualBin;
import tech.kayys.erp.warehouse.domain.model.WarehouseDigitalTwin.VirtualZone;

import javax.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for running simulations on warehouse digital twins.
 */
@ApplicationScoped
public class DigitalTwinSimulationService {

    /**
     * Simulates picking optimization.
     */
    public SimulationResult simulatePickingOptimization(
            WarehouseDigitalTwin twin,
            List<String> productIds,
            int quantity) {
        
        long startTime = System.currentTimeMillis();
        List<VirtualBin> eligibleBins = findEligibleBins(twin, productIds, quantity);
        
        // Calculate optimal picking path
        List<VirtualBin> optimizedPath = optimizePickPath(eligibleBins);
        
        long endTime = System.currentTimeMillis();
        
        return new SimulationResult(
            "PICKING_OPTIMIZATION",
            "Success",
            endTime - startTime,
            optimizedPath.size(),
            calculateDistance(optimizedPath),
            "Optimal picking path found",
            Instant.now()
        );
    }

    /**
     * Simulates putaway optimization.
     */
    public SimulationResult simulatePutawayOptimization(
            WarehouseDigitalTwin twin,
            String productId,
            int quantity,
            double minVolume) {
        
        long startTime = System.currentTimeMillis();
        List<VirtualBin> availableBins = findAvailableBins(twin, quantity, minVolume);
        
        // Calculate optimal putaway location
        VirtualBin optimalBin = findOptimalPutawayBin(availableBins, productId);
        
        long endTime = System.currentTimeMillis();
        
        return new SimulationResult(
            "PUTAWAY_OPTIMIZATION",
            "Success",
            endTime - startTime,
            optimalBin != null ? 1 : 0,
            calculateDistanceToBin(optimalBin, twin),
            optimalBin != null ? "Optimal bin found: " + optimalBin.getFullLocation() : "No suitable bin found",
            Instant.now()
        );
    }

    /**
     * Simulates warehouse layout optimization.
     */
    public SimulationResult simulateLayoutOptimization(WarehouseDigitalTwin twin) {
        long startTime = System.currentTimeMillis();
        
        // Analyze current layout
        Map<String, Object> layoutAnalysis = analyzeLayout(twin);
        
        // Generate optimized layout suggestions
        List<LayoutSuggestion> suggestions = generateLayoutSuggestions(twin, layoutAnalysis);
        
        long endTime = System.currentTimeMillis();
        
        return new SimulationResult(
            "LAYOUT_OPTIMIZATION",
            "Success",
            endTime - startTime,
            suggestions.size(),
            0.0,
            "Generated " + suggestions.size() + " layout optimization suggestions",
            Instant.now()
        );
    }

    /**
     * Simulates inventory forecasting.
     */
    public SimulationResult simulateInventoryForecasting(
            WarehouseDigitalTwin twin,
            String productId,
            int days) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> forecast = generateForecast(twin, productId, days);
        
        long endTime = System.currentTimeMillis();
        
        return new SimulationResult(
            "INVENTORY_FORECASTING",
            "Success",
            endTime - startTime,
            0,
            0.0,
            "Forecast generated for " + days + " days",
            Instant.now()
        );
    }

    private List<VirtualBin> findEligibleBins(WarehouseDigitalTwin twin, List<String> productIds, int quantity) {
        return twin.getVirtualBins().stream()
            .filter(bin -> bin.isOccupied() && productIds.contains(bin.getProductId()))
            .filter(bin -> bin.getOccupied() >= quantity)
            .collect(Collectors.toList());
    }

    private List<VirtualBin> optimizePickPath(List<VirtualBin> bins) {
        // Sort by proximity (simplified - in real implementation, use pathfinding algorithms)
        return bins.stream()
            .sorted(Comparator.comparingDouble(bin -> Math.sqrt(
                Math.pow(bin.getXCoordinate(), 2) + 
                Math.pow(bin.getYCoordinate(), 2)
            )))
            .collect(Collectors.toList());
    }

    private List<VirtualBin> findAvailableBins(WarehouseDigitalTwin twin, int quantity, double minVolume) {
        return twin.getVirtualBins().stream()
            .filter(bin -> !bin.isOccupied())
            .filter(bin -> bin.getCapacity() >= quantity)
            .sorted(Comparator.comparingDouble(VirtualBin::getAvailable))
            .collect(Collectors.toList());
    }

    private VirtualBin findOptimalPutawayBin(List<VirtualBin> availableBins, String productId) {
        return availableBins.isEmpty() ? null : availableBins.get(0);
    }

    private double calculateDistance(List<VirtualBin> path) {
        if (path.size() < 2) {
            return 0.0;
        }
        double totalDistance = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            VirtualBin current = path.get(i);
            VirtualBin next = path.get(i + 1);
            totalDistance += Math.sqrt(
                Math.pow(next.getXCoordinate() - current.getXCoordinate(), 2) +
                Math.pow(next.getYCoordinate() - current.getYCoordinate(), 2)
            );
        }
        return totalDistance;
    }

    private double calculateDistanceToBin(VirtualBin bin, WarehouseDigitalTwin twin) {
        if (bin == null) {
            return 0.0;
        }
        // Simplified - find nearest entry point
        return Math.sqrt(Math.pow(bin.getXCoordinate(), 2) + Math.pow(bin.getYCoordinate(), 2));
    }

    private Map<String, Object> analyzeLayout(WarehouseDigitalTwin twin) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Analyze zone utilization
        Map<String, Double> zoneUtilization = twin.getVirtualZones().stream()
            .collect(Collectors.toMap(
                VirtualZone::getName,
                zone -> {
                    List<VirtualBin> zoneBins = twin.getVirtualBins().stream()
                        .filter(bin -> zone.getId().equals(bin.getZone()))
                        .collect(Collectors.toList());
                    if (zoneBins.isEmpty()) {
                        return 0.0;
                    }
                    long occupied = zoneBins.stream().filter(VirtualBin::isOccupied).count();
                    return (double) occupied / zoneBins.size() * 100.0;
                }
            ));
        
        analysis.put("zoneUtilization", zoneUtilization);
        analysis.put("totalBins", twin.getVirtualBinCount());
        analysis.put("occupiedBins", twin.getVirtualBins().stream().filter(VirtualBin::isOccupied).count());
        analysis.put("utilization", twin.getUtilization());
        
        return analysis;
    }

    private List<LayoutSuggestion> generateLayoutSuggestions(
            WarehouseDigitalTwin twin,
            Map<String, Object> analysis) {
        
        List<LayoutSuggestion> suggestions = new ArrayList<>();
        
        // Check for underutilized zones
        Map<String, Double> zoneUtil = (Map<String, Double>) analysis.get("zoneUtilization");
        zoneUtil.forEach((zoneName, utilization) -> {
            if (utilization < 30.0) {
                suggestions.add(new LayoutSuggestion(
                    "ZONE_UNDERUTILIZED",
                    zoneName,
                    "Zone '" + zoneName + "' is underutilized (" + utilization + "%)",
                    "Consider redistributing inventory or repurposing the zone"
                ));
            }
            if (utilization > 90.0) {
                suggestions.add(new LayoutSuggestion(
                    "ZONE_OVERUTILIZED",
                    zoneName,
                    "Zone '" + zoneName + "' is overutilized (" + utilization + "%)",
                    "Consider expanding the zone or redistributing inventory"
                ));
            }
        });
        
        return suggestions;
    }

    private Map<String, Object> generateForecast(WarehouseDigitalTwin twin, String productId, int days) {
        Map<String, Object> forecast = new HashMap<>();
        
        // Get current stock for product
        double currentStock = twin.getVirtualBins().stream()
            .filter(bin -> productId.equals(bin.getProductId()))
            .mapToDouble(VirtualBin::getOccupied)
            .sum();
        
        // Simple forecasting (in real implementation, use ML algorithms)
        double dailyUsage = currentStock / 30.0; // Assume 30 days of data
        double projectedStock = currentStock - (dailyUsage * days);
        
        forecast.put("productId", productId);
        forecast.put("currentStock", currentStock);
        forecast.put("dailyUsage", dailyUsage);
        forecast.put("projectedStock", Math.max(0, projectedStock));
        forecast.put("days", days);
        forecast.put("reorderRecommended", projectedStock < 0);
        
        return forecast;
    }

    /**
     * Simulation result record.
     */
    public record SimulationResult(
        String simulationType,
        String status,
        long durationMs,
        int itemsProcessed,
        double distanceOptimized,
        String summary,
        Instant timestamp
    ) {}

    /**
     * Layout suggestion record.
     */
    public record LayoutSuggestion(
        String type,
        String target,
        String description,
        String recommendation
    ) {}
}