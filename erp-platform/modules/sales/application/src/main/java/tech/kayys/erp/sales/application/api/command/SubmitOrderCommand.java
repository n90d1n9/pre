package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.OrderId;

/**
 * Command to submit an order for processing.
 */
public record SubmitOrderCommand(
        OrderId orderId
) implements Command<OrderId> {

    public SubmitOrderCommand {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
    }
}