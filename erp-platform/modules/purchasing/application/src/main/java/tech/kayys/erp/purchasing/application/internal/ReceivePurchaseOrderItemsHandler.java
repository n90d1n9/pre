package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.ReceivePurchaseOrderItemsCommand;
import tech.kayys.erp.purchasing.application.port.InventoryPort;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;
import tech.kayys.erp.purchasing.domain.repository.PurchaseOrderRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for receiving purchase order items.
 */
@UseCase("Receive items for a purchase order")
public class ReceivePurchaseOrderItemsHandler 
        implements CommandHandler<ReceivePurchaseOrderItemsCommand, PurchaseOrderId> {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryPort inventoryPort;

    @Inject
    public ReceivePurchaseOrderItemsHandler(
            PurchaseOrderRepository purchaseOrderRepository,
            InventoryPort inventoryPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.inventoryPort = inventoryPort;
    }

    @Override
    public CompletionStage<PurchaseOrderId> handle(ReceivePurchaseOrderItemsCommand command) {
        return purchaseOrderRepository.findById(command.purchaseOrderId())
            .thenCompose(poOpt -> {
                if (poOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Purchase Order not found: " + command.purchaseOrderId())
                    );
                }

                PurchaseOrder po = poOpt.get();

                // Convert to domain received items
                List<PurchaseOrder.ReceivedItem> receivedItems = command.receivedItems().stream()
                    .map(item -> new PurchaseOrder.ReceivedItem(
                        item.itemIndex(),
                        item.quantityReceived(),
                        item.notes()
                    ))
                    .collect(java.util.stream.Collectors.toList());

                // Receive the items (domain logic)
                po.receiveItems(receivedItems);

                // Update inventory
                return inventoryPort.receivePurchaseOrder(po)
                    .thenCompose(v -> {
                        // Save the updated PO
                        return purchaseOrderRepository.save(po)
                            .thenApply(PurchaseOrder::getId);
                    });
            });
    }
}