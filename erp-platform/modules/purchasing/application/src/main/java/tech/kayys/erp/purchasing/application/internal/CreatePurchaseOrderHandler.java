package tech.kayys.erp.purchasing.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.purchasing.application.api.command.CreatePurchaseOrderCommand;
import tech.kayys.erp.purchasing.application.port.VendorPort;
import tech.kayys.erp.purchasing.application.port.InventoryPort;
import tech.kayys.erp.purchasing.domain.identifier.PurchaseOrderId;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.model.PurchaseOrder;
import tech.kayys.erp.purchasing.domain.repository.PurchaseOrderRepository;
import tech.kayys.erp.purchasing.domain.valueobject.Money;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for creating purchase orders.
 */
@UseCase("Create a new purchase order")
public class CreatePurchaseOrderHandler 
        implements CommandHandler<CreatePurchaseOrderCommand, PurchaseOrderId> {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final VendorPort vendorPort;
    private final InventoryPort inventoryPort;

    @Inject
    public CreatePurchaseOrderHandler(
            PurchaseOrderRepository purchaseOrderRepository,
            VendorPort vendorPort,
            InventoryPort inventoryPort) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.vendorPort = vendorPort;
        this.inventoryPort = inventoryPort;
    }

    @Override
    public CompletionStage<PurchaseOrderId> handle(CreatePurchaseOrderCommand command) {
        // 1. Validate vendor exists
        return vendorPort.validateVendor(command.vendorId())
            .thenCompose(valid -> {
                if (!valid) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Vendor not found: " + command.vendorId())
                    );
                }

                // 2. Validate product inventory requirements
                return validateInventory(command)
                    .thenCompose(validInventory -> {
                        if (!validInventory) {
                            return CompletableFuture.failedFuture(
                                new IllegalArgumentException("Insufficient inventory or product not found")
                            );
                        }

                        // 3. Generate PO number
                        return purchaseOrderRepository.generatePoNumber()
                            .thenApply(poNumber -> {
                                // 4. Create the purchase order
                                PurchaseOrder po = PurchaseOrder.create(
                                    command.purchaseOrderId(),
                                    poNumber,
                                    VendorId.of(command.vendorId()),
                                    command.vendorName(),
                                    command.requiredDate(),
                                    command.currencyCode()
                                );

                                // 5. Add items
                                for (CreatePurchaseOrderCommand.PurchaseOrderItemCommand itemCommand : command.items()) {
                                    Money unitPrice = Money.of(
                                        new BigDecimal(itemCommand.unitPrice()),
                                        command.currencyCode()
                                    );

                                    PurchaseOrder.PurchaseOrderItem item = 
                                        new PurchaseOrder.PurchaseOrderItem(
                                            itemCommand.productId(),
                                            itemCommand.productName(),
                                            itemCommand.sku(),
                                            itemCommand.quantity(),
                                            unitPrice,
                                            itemCommand.uom()
                                        );
                                    po.addItem(item);
                                }

                                // 6. Set additional fields
                                if (command.shippingAddress() != null) {
                                    po.setShippingAddress(command.shippingAddress());
                                }
                                if (command.billingAddress() != null) {
                                    po.setBillingAddress(command.billingAddress());
                                }
                                if (command.paymentTerms() != null) {
                                    po.setPaymentTerms(command.paymentTerms());
                                }
                                if (command.shippingTerms() != null) {
                                    po.setShippingTerms(command.shippingTerms());
                                }
                                if (command.notes() != null) {
                                    po.setNotes(command.notes());
                                }
                                if (command.createdBy() != null) {
                                    po.setCreatedBy(command.createdBy());
                                }

                                return po;
                            })
                            .thenCompose(po -> purchaseOrderRepository.save(po)
                                .thenApply(PurchaseOrder::getId));
                    });
            });
    }

    private CompletionStage<Boolean> validateInventory(CreatePurchaseOrderCommand command) {
        // Validate each product exists and check if we need to purchase
        // This would check against Inventory context
        return CompletableFuture.completedFuture(true);
    }
}