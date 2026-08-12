package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to cancel an order.
 */
public record CancelOrderCommand(
        OrderId orderId,
        String reason
) implements Command<OrderId> {

    public CancelOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Cancellation reason is required");
        }
    }
}