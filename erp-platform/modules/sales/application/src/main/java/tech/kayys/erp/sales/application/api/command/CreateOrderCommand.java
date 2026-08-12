package tech.kayys.erp.sales.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.sales.domain.identifier.CustomerId;
import tech.kayys.erp.sales.domain.identifier.OrderId;

import java.util.List;
import java.util.UUID;

/**
 * Command to create a new order.
 */
public record CreateOrderCommand(
        OrderId orderId,
        CustomerId customerId,
        List<OrderItemCommand> items,
        AddressCommand shippingAddress,
        AddressCommand billingAddress,
        String customerNotes
) implements Command<OrderId> {

    public CreateOrderCommand {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }
    }

    /**
     * Creates a builder for the command.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private List<OrderItemCommand> items;
        private AddressCommand shippingAddress;
        private AddressCommand billingAddress;
        private String customerNotes;

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder items(List<OrderItemCommand> items) {
            this.items = items;
            return this;
        }

        public Builder shippingAddress(AddressCommand shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder billingAddress(AddressCommand billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        public Builder customerNotes(String customerNotes) {
            this.customerNotes = customerNotes;
            return this;
        }

        public CreateOrderCommand build() {
            if (orderId == null) {
                orderId = OrderId.generate();
            }
            if (billingAddress == null) {
                billingAddress = shippingAddress;
            }
            return new CreateOrderCommand(
                orderId, customerId, items, 
                shippingAddress, billingAddress, customerNotes
            );
        }
    }

    /**
     * Order item command.
     */
    public record OrderItemCommand(
            UUID productId,
            String productName,
            String sku,
            int quantity,
            MoneyCommand unitPrice
    ) {
        public OrderItemCommand {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (productName == null || productName.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice == null) {
                throw new IllegalArgumentException("Unit price is required");
            }
        }
    }

    /**
     * Address command.
     */
    public record AddressCommand(
            String street,
            String city,
            String state,
            String postalCode,
            String country
    ) {
        public AddressCommand {
            if (street == null || street.trim().isEmpty()) {
                throw new IllegalArgumentException("Street cannot be empty");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new IllegalArgumentException("City cannot be empty");
            }
            if (country == null || country.trim().isEmpty()) {
                throw new IllegalArgumentException("Country cannot be empty");
            }
        }
    }

    /**
     * Money command.
     */
    public record MoneyCommand(
            String amount,
            String currencyCode
    ) {
        public MoneyCommand {
            if (amount == null || amount.trim().isEmpty()) {
                throw new IllegalArgumentException("Amount cannot be empty");
            }
            if (currencyCode == null || currencyCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Currency code cannot be empty");
            }
        }
    }
}