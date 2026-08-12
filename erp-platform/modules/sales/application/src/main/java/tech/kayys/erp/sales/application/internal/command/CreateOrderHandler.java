package tech.kayys.erp.sales.application.internal.command;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.sales.application.api.command.CreateOrderCommand;
import tech.kayys.erp.sales.application.port.ProductPricePort;
import tech.kayys.erp.sales.domain.identifier.OrderId;
import tech.kayys.erp.sales.domain.model.Order;
import tech.kayys.erp.sales.domain.model.OrderItem;
import tech.kayys.erp.sales.domain.repository.OrderRepository;
import tech.kayys.erp.sales.domain.valueobject.Address;
import tech.kayys.erp.sales.domain.valueobject.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Handler for creating orders.
 */
@UseCase("Create a new order")
public class CreateOrderHandler implements CommandHandler<CreateOrderCommand, OrderId> {

    private final OrderRepository orderRepository;
    private final ProductPricePort productPricePort;

    @Inject
    public CreateOrderHandler(OrderRepository orderRepository, ProductPricePort productPricePort) {
        this.orderRepository = orderRepository;
        this.productPricePort = productPricePort;
    }

    @Override
    public CompletionStage<OrderId> handle(CreateOrderCommand command) {
        // 1. Validate customer exists (would use a customer service)
        // This would call the CRM context through a port

        // 2. Create the order
        Order order = Order.create(command.orderId(), command.customerId());

        // 3. Convert and add items
        List<OrderItem> items = command.items().stream()
            .map(this::toOrderItem)
            .collect(Collectors.toList());

        for (OrderItem item : items) {
            // Validate product availability (would call Inventory)
            // This is a cross-context interaction
            order.addItem(item);
        }

        // 4. Set addresses
        Address shippingAddress = toAddress(command.shippingAddress());
        Address billingAddress = toAddress(command.billingAddress());
        
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);

        // 5. Set customer notes
        if (command.customerNotes() != null) {
            order.setCustomerNotes(command.customerNotes());
        }

        // 6. Save the order
        return orderRepository.save(order)
            .thenApply(Order::getId);
    }

    private OrderItem toOrderItem(CreateOrderCommand.OrderItemCommand itemCommand) {
        Money unitPrice = Money.of(
            new BigDecimal(itemCommand.unitPrice().amount()),
            itemCommand.unitPrice().currencyCode()
        );

        return OrderItem.builder()
            .productId(itemCommand.productId())
            .productName(itemCommand.productName())
            .sku(itemCommand.sku())
            .quantity(itemCommand.quantity())
            .unitPrice(unitPrice)
            .build();
    }

    private Address toAddress(CreateOrderCommand.AddressCommand addressCommand) {
        return Address.of(
            addressCommand.street(),
            addressCommand.city(),
            addressCommand.state(),
            addressCommand.postalCode(),
            addressCommand.country()
        );
    }
}