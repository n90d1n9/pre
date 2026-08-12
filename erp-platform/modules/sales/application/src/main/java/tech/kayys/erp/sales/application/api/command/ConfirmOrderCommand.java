package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to confirm an order.
 */
public record ConfirmOrderCommand(
        OrderId orderId
) implements Command<OrderId> {

    public ConfirmOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
    }
}