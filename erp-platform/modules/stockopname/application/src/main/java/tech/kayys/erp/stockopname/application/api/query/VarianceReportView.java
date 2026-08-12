package tech.kayys.erp.stockopname.application.api.query;

import tech.kayys.erp.stockopname.domain.model.CountingSession;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Variance report view.
 */
public record VarianceReportView(
        String sessionId,
        String sessionNumber,
        String warehouseName,
        int totalItemsChecked,
        int itemsWithVariance,
        int itemsWithoutVariance,
        int itemsWithApprovedVariance,
        int itemsWithRejectedVariance,
        int totalVarianceQuantity,
        double totalVarianceValue,
        double averageVariance,
        String currencyCode,
        List<VarianceDetail> varianceDetails
) {

    public static VarianceReportView fromDomain(CountingSession session, String currencyCode) {
        List<CountingSession.CountingItem> itemsWithVariance = session.getItemsWithVariance();
        
        int totalVariance = itemsWithVariance.stream()
            .mapToInt(CountingSession.CountingItem::getVariance)
            .sum();
        
        double totalVarianceValue = itemsWithVariance.stream()
            .mapToDouble(CountingSession.CountingItem::getVariance)
            .sum();
        
        return new VarianceReportView(
            session.getId().toString(),
            session.getSessionNumber(),
            session.getWarehouseName(),
            session.getTotalItemsToCount(),
            session.getItemsWithVariance(),
            session.getVerifiedItems() - session.getItemsWithVariance(),
            (int) itemsWithVariance.stream()
                .filter(i -> i.getVarianceStatus().name().equals("APPROVED"))
                .count(),
            (int) itemsWithVariance.stream()
                .filter(i -> i.getVarianceStatus().name().equals("REJECTED"))
                .count(),
            totalVariance,
            totalVarianceValue,
            itemsWithVariance.isEmpty() ? 0.0 : totalVarianceValue / itemsWithVariance.size(),
            currencyCode,
            itemsWithVariance.stream()
                .map(VarianceDetail::fromDomain)
                .collect(Collectors.toList())
        );
    }

    public record VarianceDetail(
            String productId,
            String sku,
            String productName,
            String binLocation,
            int systemQuantity,
            int countedQuantity,
            int varianceQuantity,
            double varianceValue,
            String status,
            String notes
    ) {
        public static VarianceDetail fromDomain(CountingSession.CountingItem item) {
            return new VarianceDetail(
                item.getProductId(),
                item.getSku(),
                item.getProductName(),
                item.getBinLocation(),
                item.getSystemQuantity(),
                item.getCountedQuantity() != null ? item.getCountedQuantity() : 0,
                item.getVariance(),
                0.0, // Would need unit cost
                item.getVarianceStatus().name(),
                item.getVarianceNotes()
            );
        }
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>
</modules>