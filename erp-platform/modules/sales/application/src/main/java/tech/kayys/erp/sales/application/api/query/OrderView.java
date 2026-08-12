package tech.kayys.erp.sales.application.api.query;

import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.model.OrderItem;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only view of an order.
 */
public record OrderView(
        String orderId,
        String customerId,
        List<OrderItemView> items,
        String subtotal,
        String taxTotal,
        String shippingCost,
        String discountTotal,
        String grandTotal,
        String currency,
        String status,
        AddressView shippingAddress,
        AddressView billingAddress,
        String customerNotes,
        String trackingNumber,
        String shippingMethod,
        Instant createdAt,
        Instant submittedAt,
        Instant confirmedAt,
        Instant shippedAt,
        Instant deliveredAt,
        int itemCount
) {

    public static OrderView fromDomain(Order order) {
        List<OrderItemView> items = order.getItems().stream()
            .map(OrderItemView::fromDomain)
            .collect(Collectors.toList());

        return new OrderView(
            order.getId().toString(),
            order.getCustomerId().toString(),
            items,
            order.getSubtotal().getAmount().toPlainString(),
            order.getTaxTotal().getAmount().toPlainString(),
            order.getShippingCost().getAmount().toPlainString(),
            order.getDiscountTotal().getAmount().toPlainString(),
            order.getGrandTotal().getAmount().toPlainString(),
            order.getGrandTotal().getCurrency().getCurrencyCode(),
            order.getStatus().name(),
            AddressView.fromDomain(order.getShippingAddress()),
            AddressView.fromDomain(order.getBillingAddress()),
            order.getCustomerNotes(),
            order.getTrackingNumber(),
            order.getShippingMethod(),
            order.getCreatedAt(),
            order.getSubmittedAt(),
            order.getConfirmedAt(),
            order.getShippedAt(),
            order.getDeliveredAt(),
            order.getItems().size()
        );
    }

    public record OrderItemView(
            String productId,
            String productName,
            String sku,
            int quantity,
            String unitPrice,
            String totalPrice,
            String currency
    ) {
        public static OrderItemView fromDomain(OrderItem item) {
            return new OrderItemView(
                item.getProductId().toString(),
                item.getProductName(),
                item.getSku(),
                item.getQuantity(),
                item.getUnitPrice().getAmount().toPlainString(),
                item.getTotalPrice().getAmount().toPlainString(),
                item.getTotalPrice().getCurrency().getCurrencyCode()
            );
        }
    }

    public record AddressView(
            String street,
            String city,
            String state,
            String postalCode,
            String country
    ) {
        public static AddressView fromDomain(tech.kayys.erp.sales.domain.valueobject.Address address) {
            if (address == null) {
                return null;
            }
            return new AddressView(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry()
            );
        }
    }
}