package tech.kayys.erp.sales.application.internal.command;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.sales.application.api.command.SubmitOrderCommand;
import tech.kayys.erp.sales.application.port.OrderEventPublisher;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.repository.OrderRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Handler for submitting orders.
 */
@UseCase("Submit an order for processing")
public class SubmitOrderHandler implements CommandHandler<SubmitOrderCommand, OrderId> {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;

    @Inject
    public SubmitOrderHandler(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public CompletionStage<OrderId> handle(SubmitOrderCommand command) {
        return orderRepository.findById(command.orderId())
            .thenCompose(orderOpt -> {
                if (orderOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Order not found: " + command.orderId())
                    );
                }

                Order order = orderOpt.get();
                
                // Submit the order (domain logic)
                order.submit();

                // Save the updated order
                return orderRepository.save(order)
                    .thenCompose(saved -> {
                        // Publish events
                        return eventPublisher.publishAllEvents(saved)
                            .thenApply(v -> saved.getId());
                    });
            });
    }
}