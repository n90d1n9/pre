package tech.kayys.erp.stockopname.application.api.query;

import tech.kayys.erp.stockopname.domain.model.CountingSession;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * View of a counting session.
 */
public record CountingSessionView(
        String sessionId,
        String sessionNumber,
        String warehouseId,
        String warehouseName,
        String countingType,
        String countingMethod,
        String status,
        String zone,
        List<String> categories,
        String scheduledDate,
        String startDate,
        String completionDate,
        int totalItemsToCount,
        int countedItems,
        int verifiedItems,
        int itemsWithVariance,
        double completionPercentage,
        double verificationPercentage,
        double variancePercentage,
        List<CountingItemView> items,
        String notes,
        String createdBy,
        String verifiedBy,
        String adjustedBy
) {

    public static CountingSessionView fromDomain(CountingSession session) {
        return new CountingSessionView(
            session.getId().toString(),
            session.getSessionNumber(),
            session.getWarehouseId().toString(),
            session.getWarehouseName(),
            session.getCountingType().name(),
            session.getCountingMethod().name(),
            session.getStatus().name(),
            session.getZone(),
            session.getCategories(),
            session.getScheduledDate().toString(),
            session.getStartDate() != null ? session.getStartDate().toString() : null,
            session.getCompletionDate() != null ? session.getCompletionDate().toString() : null,
            session.getTotalItemsToCount(),
            session.getCountedItems(),
            session.getVerifiedItems(),
            session.getItemsWithVariance(),
            session.getCompletionPercentage(),
            session.getVerificationPercentage(),
            session.getVariancePercentage(),
            session.getCountingItems().stream()
                .map(CountingItemView::fromDomain)
                .collect(Collectors.toList()),
            session.getNotes(),
            session.getCreatedBy(),
            session.getVerifiedBy(),
            session.getAdjustedBy()
        );
    }
}