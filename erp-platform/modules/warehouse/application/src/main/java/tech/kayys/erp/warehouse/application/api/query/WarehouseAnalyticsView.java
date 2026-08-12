package tech.kayys.erp.warehouse.application.api.query;

import java.time.Instant;
import java.util.List;

/**
 * Warehouse analytics view.
 */
public record WarehouseAnalyticsView(
        // Warehouse Summary
        int totalWarehouses,
        int activeWarehouses,
        int totalBinLocations,
        int occupiedBinLocations,
        double overallUtilization,
        
        // Inventory Summary
        int totalStockItems,
        int totalStockQuantity,
        int reservedQuantity,
        int availableQuantity,
        
        // Activity Summary
        int receivingTasksToday,
        int pickingTasksToday,
        int putawayTasksToday,
        int shippingTasksToday,
        int transfersToday,
        
        // Performance Metrics
        double averageReceivingTime,
        double averagePickingTime,
        double averagePutawayTime,
        double averageShippingTime,
        double orderFillRate,
        double accuracyRate,
        
        // Trending
        List<DailyActivity> last7DaysActivity,
        List<WarehouseUtilization> warehouseUtilization,
        
        // Period Information
        String periodStart,
        String periodEnd,
        Instant updatedAt
) {

    public record DailyActivity(
            String date,
            int receivingCount,
            int pickingCount,
            int putawayCount,
            int shippingCount
    ) {}

    public record WarehouseUtilization(
            String warehouseId,
            String warehouseName,
            double utilization,
            int totalBins,
            int occupiedBins
    ) {}
}