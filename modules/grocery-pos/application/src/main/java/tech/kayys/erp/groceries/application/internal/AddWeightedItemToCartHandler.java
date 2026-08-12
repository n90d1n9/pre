package tech.kayys.erp.groceries.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.groceries.application.api.CartItemResult;
import tech.kayys.erp.groceries.application.api.command.AddWeightedItemCommand;
import tech.kayys.erp.groceries.domain.model.GroceryProduct;
import tech.kayys.erp.groceries.domain.model.ScaleDevice;
import tech.kayys.erp.groceries.domain.repository.GroceryProductRepository;
import tech.kayys.erp.groceries.domain.repository.ScaleDeviceRepository;
import tech.kayys.erp.groceries.domain.valueobject.Weight;
import tech.kayys.erp.sales.domain.identifier.CartId;
import tech.kayys.erp.sales.domain.model.Cart;
import tech.kayys.erp.sales.domain.repository.CartRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@UseCase("Add a weighted grocery item to the cart")
public class AddWeightedItemToCartHandler implements CommandHandler<AddWeightedItemCommand, CartItemResult> {

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
        return groceryProductRepository.findById(command.groceryProductId())
            .thenCompose(productOpt -> {
                if (productOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Grocery product not found: " + command.groceryProductId())
                    );
                }
                GroceryProduct product = productOpt.get();
                if (!product.isWeightBased()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Product is not weight-based")
                    );
                }
                return scaleDeviceRepository.findById(command.scaleId())
                    .thenCompose(scaleOpt -> {
                        if (scaleOpt.isEmpty()) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Scale not found: " + command.scaleId())
                            );
                        }
                        ScaleDevice scale = scaleOpt.get();
                        if (!scale.isConnected()) {
                            return CompletableFuture.failedFuture(
                                new IllegalStateException("Scale is not connected")
                            );
                        }
                        Weight weight = command.weight() != null ? command.weight() : Weight.zero();
                        if (!scale.isValidWeight(weight)) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Weight is outside valid range")
                            );
                        }
                        Weight netWeight = scale.getNetWeight(weight);
                        return cartRepository.findById(command.cartId())
                            .thenCompose(cartOpt -> {
                                if (cartOpt.isEmpty()) {
                                    return CompletableFuture.failedFuture(
                                        new IllegalArgumentException("Cart not found: " + command.cartId())
                                    );
                                }
                                Cart cart = cartOpt.get();
                                return cartRepository.save(cart)
                                    .thenApply(v -> new CartItemResult(
                                        command.cartId().toString(),
                                        product.getCatalogProductId().toString(),
                                        product.getProductType().name(),
                                        netWeight.getValue().doubleValue(),
                                        netWeight.getUnit().getSymbol(),
                                        0.0, 0.0, true
                                    ));
                            });
                    });
            });
    }
}
