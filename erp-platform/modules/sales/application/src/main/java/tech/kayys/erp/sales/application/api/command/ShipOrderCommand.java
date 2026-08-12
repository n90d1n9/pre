package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to mark an order as shipped.
 */
public record ShipOrderCommand(
        OrderId orderId,
        String trackingNumber,
        String shippingMethod
) implements Command<OrderId> {

    public ShipOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (trackingNumber == null || trackingNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking number is required");
        }
        if (shippingMethod == null || shippingMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping method is required");
        }
    }
}