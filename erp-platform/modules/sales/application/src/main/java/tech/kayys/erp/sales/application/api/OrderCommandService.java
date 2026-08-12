package tech.kayys.erp.sales.application.api;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.sales.application.api.command.*;
import tech.kayys.erp.sales.domain.identifier.OrderId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for order commands.
 */
public interface OrderCommandService {

    /**
     * Creates a new order.
     */
    CompletionStage<OrderId> createOrder(CreateOrderCommand command);

    /**
     * Submits an order for processing.
     */
    CompletionStage<OrderId> submitOrder(SubmitOrderCommand command);

    /**
     * Confirms an order.
     */
    CompletionStage<OrderId> confirmOrder(ConfirmOrderCommand command);

    /**
     * Ships an order.
     */
    CompletionStage<OrderId> shipOrder(ShipOrderCommand command);

    /**
     * Cancels an order.
     */
    CompletionStage<OrderId> cancelOrder(CancelOrderCommand command);
}