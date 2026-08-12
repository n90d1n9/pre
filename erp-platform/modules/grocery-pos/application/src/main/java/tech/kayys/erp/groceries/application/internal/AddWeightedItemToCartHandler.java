package tech.kayys.erp.groceries.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.groceries.application.api.command.AddWeightedItemCommand;
import tech.kayys.erp.groceries.application.api.CartItemResult;
import tech.kayys.erp.groceries.domain.model.GroceryProduct;
import tech.kayys.erp.groceries.domain.model.ScaleDevice;
import tech.kayys.erp.groceries.domain.repository.GroceryProductRepository;
import tech.kayys.erp.groceries.domain.repository.ScaleDeviceRepository;
import tech.kayys.erp.groceries.domain.valueobject.Weight;
import tech.kayys.erp.sales.domain.model.Cart;
import tech.kayys.erp.sales.domain.repository.CartRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for adding weighted items to the cart.
 */
@UseCase("Add a weighted grocery item to the cart")
public class AddWeightedItemToCartHandler
        implements CommandHandler<AddWeightedItemCommand, CartItemResult> {

    private final GroceryProductRepository groceryProductRepository;
    private final ScaleDeviceRepository scaleDeviceRepository;
    private final CartRepository cartRepository;

    @Inject
    public AddWeightedItemToCartHandler(
            GroceryProductRepository groceryProductRepository,
            ScaleDeviceRepository scaleDeviceRepository,
            CartRepository cartRepository) {
        this.groceryProductRepository = groceryProductRepository;
        this.scaleDeviceRepository = scaleDeviceRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CompletionStage<CartItemResult> handle(AddWeightedItemCommand command) {
        // 1. Get the grocery product
        return groceryProductRepository.findById(command.groceryProductId())
            .thenCompose(productOpt -> {
                if (productOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Grocery product not found: " + command.groceryProductId())
                    );
                }

                GroceryProduct product = productOpt.get();

                // 2. Validate product is weight-based
                if (!product.isWeightBased()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Product is not weight-based")
                    );
                }

                // 3. Get the scale device
                return scaleDeviceRepository.findById(command.scaleId())
                    .thenCompose(scaleOpt -> {
                        if (scaleOpt.isEmpty()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Scale not found: " + command.scaleId())
                            );
                        }

                        ScaleDevice scale = scaleOpt.get();

                        // 4. Validate scale is connected
                        if (!scale.isConnected()) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Scale is not connected")
                            );
                        }

                        // 5. Get the weight from the scale
                        // In real implementation, this would read from the physical scale
                        Weight weight = command.weight() != null ?
                            command.weight() : Weight.zero();

                        // 6. Validate weight is within range
                        if (!scale.isValidWeight(weight)) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Weight is outside valid range")
                            );
                        }

                        // 7. Calculate net weight (gross - tare)
                        Weight netWeight = scale.getNetWeight(weight);

                        // 8. Get the cart
                        return cartRepository.findById(command.cartId())
                            .thenCompose(cartOpt -> {
                                if (cartOpt.isEmpty()) {
                                    return CompletableFuture.failedFuture(
                                        new IllegalArgumentException("Cart not found: " + command.cartId())
                                    );
                                }

                                Cart cart = cartOpt.get();

                                // 9. Get product price
                                // This would call the Pricing context

                                // 10. Add item to cart with weight
                                // Calculate price based on weight and unit price

                                // 11. Save the cart
                                return cartRepository.save(cart)
                                    .thenApply(v -> new CartItemResult(
                                        command.cartId().toString(),
                                        product.getCatalogProductId().toString(),
                                        product.getProductType().name(),
                                        netWeight.getValue().doubleValue(),
                                        netWeight.getUnit().getSymbol(),
                                        0.0, // price
                                        0.0, // total
                                        true
                                    ));
                            });
                    });
            });
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/grocery-pos/domain</module>
    <module>modules/grocery-pos/application</module>
    <module>modules/grocery-pos/infrastructure</module>
    <module>modules/grocery-pos/interfaces</module>
</modules>
// Core Catalog Product
public final class Product extends AggregateRoot<ProductId> {
    private String name;
    private String description;
    private Money price;
    private String sku;
    private ProductStatus status;
    // ... basic product attributes
}

// Grocery POS Extends Catalog Product
public final class GroceryProduct extends AggregateRoot<GroceryProductId> {
    private UUID catalogProductId; // ← Links to core Product
    private GroceryProductType productType; // ← Grocery-specific
    private boolean isWeightBased; // ← Grocery-specific
    private Weight defaultWeight; // ← Grocery-specific
    private ShelfLife shelfLife; // ← Grocery-specific
    private List<BatchLot> batchLots; // ← Grocery-specific
    // ... grocery-specific attributes
}
// Core Inventory Context
public interface InventoryService {
    CompletionStage<StockLevel> checkStock(ProductId productId);
    CompletionStage<Void> adjustStock(AdjustStockCommand command);
}

// Grocery POS uses Inventory with enhancements
public interface GroceryInventoryService {
    // Tracks inventory by batch/lot
    CompletionStage<BatchStock> getBatchStock(String batchNumber);
    
    // Expiry-aware inventory
    CompletionStage<List<ExpiringBatch>> getExpiringBatches(int daysThreshold);
    
    // Waste management
    CompletionStage<Void> processWaste(ProcessWasteCommand command);
}
// Core Sales Cart
public final class Cart extends AggregateRoot<CartId> {
    private List<CartItem> items;
    private Money total;
    // ... standard cart operations
}

// Grocery Cart extends Core Cart
public final class GroceryCart extends Cart {
    private List<WeightedCartItem> weightedItems; // ← Grocery-specific
    private ScaleId currentScaleId; // ← Grocery-specific
    private boolean isWeightBased; // ← Grocery-specific
}
// Example: Grocery POS checkout flow using core modules
public class GroceryCheckoutService {
    
    private final SalesCommandService salesService;      // Core Sales
    private final InventoryService inventoryService;     // Core Inventory
    private final PricingService pricingService;         // Core Pricing
    private final GroceryProductRepository groceryRepo;  // Grocery POS
    
    public CompletionStage<Receipt> checkout(Cart cart) {
        // 1. Validate grocery items with fresh-specific rules
        return validateFreshItems(cart)
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Expired or invalid fresh items")
                    );
                }
                
                // 2. Get pricing from core pricing (with grocery adjustments)
                return pricingService.calculatePrice(cart)
                    .thenCompose(pricing -> {
                        // 3. Process through core sales
                        return salesService.createOrder(cart)
                            .thenCompose(order -> {
                                // 4. Update inventory (core + grocery-specific)
                                return inventoryService.adjustStock(order)
                                    .thenCompose(v -> 
                                        updateGroceryInventory(order)
                                    )
                                    .thenApply(v -> {
                                        // 5. Generate receipt
                                        return generateReceipt(order);
                                    });
                            });
                    });
            });
    }
    
    private CompletionStage<Void> updateGroceryInventory(Order order) {
        // Deduct from specific batches/lots
        for (OrderItem item : order.getItems()) {
            if (item.isWeighted()) {
                // Deduct from specific batch/lot
                return groceryRepo.deductFromBatch(
                    item.getBatchNumber(),
                    item.getQuantity()
                );
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
// Core Catalog Events
public class ProductCreated implements DomainEvent { ... }
public class ProductUpdated implements DomainEvent { ... }

// Grocery POS Events
public class BatchAdded implements DomainEvent { ... }      // Grocery-specific
public class BatchExpired implements DomainEvent { ... }    // Grocery-specific
public class ShelfLifeUpdated implements DomainEvent { ... } // Grocery-specific
public class WasteProcessed implements DomainEvent { ... }   // Grocery-specific