package tech.kayys.erp.sales.application.internal.command;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.sales.application.api.command.ConfirmOrderCommand;
import tech.kayys.erp.sales.application.port.OrderEventPublisher;
import tech.kayys.erp.sales.application.port.ReserveInventoryPort;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.repository.OrderRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for confirming orders.
 */
@UseCase("Confirm an order")
public class ConfirmOrderHandler implements CommandHandler<ConfirmOrderCommand, OrderId> {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final ReserveInventoryPort reserveInventoryPort;

    @Inject
    public ConfirmOrderHandler(
            OrderRepository orderRepository,
            OrderEventPublisher eventPublisher,
            ReserveInventoryPort reserveInventoryPort) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.reserveInventoryPort = reserveInventoryPort;
    }

    @Override
    public CompletionStage<OrderId> handle(ConfirmOrderCommand command) {
        return orderRepository.findById(command.orderId())
            .thenCompose(orderOpt -> {
                if (orderOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Order not found: " + command.orderId())
                    );
                }

                Order order = orderOpt.get();

                // 1. Reserve inventory
                return reserveInventoryPort.reserveForOrder(order)
                    .thenCompose(v -> {
                        // 2. Confirm the order
                        order.confirm();

                        // 3. Save and publish events
                        return orderRepository.save(order)
                            .thenCompose(saved -> 
                                eventPublisher.publishAllEvents(saved)
                                    .thenApply(x -> saved.getId())
                            );
                    });
            });
    }
}