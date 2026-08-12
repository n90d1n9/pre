package tech.kayys.erp.stockopname.application.api.query;

import tech.kayys.erp.stockopname.domain.model.CountingSession;

import java.time.Instant;

/**
 * View of a counting item.
 */
public record CountingItemView(
        String id,
        String productId,
        String sku,
        String productName,
        String binLocation,
        int systemQuantity,
        Integer countedQuantity,
        String countedBy,
        String countedAt,
        Integer secondCountedQuantity,
        String secondCountedBy,
        boolean verified,
        String verifiedBy,
        String verifiedAt,
        boolean completed,
        String varianceStatus,
        int variance,
        double variancePercentage,
        String varianceNotes
) {

    public static CountingItemView fromDomain(CountingSession.CountingItem item) {
        return new CountingItemView(
            item.getId(),
            item.getProductId(),
            item.getSku(),
            item.getProductName(),
            item.getBinLocation(),
            item.getSystemQuantity(),
            item.getCountedQuantity(),
            item.getCountedBy(),
            item.getCountedAt() != null ? item.getCountedAt().toString() : null,
            item.getSecondCountedQuantity(),
            item.getSecondCountedBy(),
            item.isVerified(),
            item.getVerifiedBy(),
            item.getVerifiedAt() != null ? item.getVerifiedAt().toString() : null,
            item.isCompleted(),
            item.getVarianceStatus() != null ? item.getVarianceStatus().name() : null,
            item.getVariance(),
            item.getVariancePercentage(),
            item.getVarianceNotes()
        );
    }
}