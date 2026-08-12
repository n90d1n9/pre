package tech.kayys.erp.billing.application.service;

import io.smallrye.mutiny.Uni;
import tech.kayys.erp.billing.application.port.*;
import tech.kayys.erp.billing.plugin.BillingPlugin;
import tech.kayys.erp.billing.plugin.ProductType;
import tech.kayys.erp.billing.plugin.model.*;
import tech.kayys.erp.billing.plugin.registry.PluginRegistry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

/**
 * Billing orchestrator that routes billing requests to the appropriate plugin.
 */
@ApplicationScoped
public class BillingOrchestrator {

    @Inject
    PluginRegistry pluginRegistry;

    @Inject
    PaymentGatewayPort paymentGatewayPort;

    @Inject
    TaxPort taxPort;

    @Inject
    NotificationPort notificationPort;

    /**
     * Processes a billing request for any product type.
     */
    public CompletionStage<BillingResult> processBilling(BillingRequest request) {
        // Determine product type
        ProductType productType = request.getProductType();
        
        // Get the appropriate plugin
        BillingPlugin plugin = pluginRegistry.getDefaultPluginForProductType(productType);
        
        // Validate product through plugin
        return plugin.validateProduct(request.getProduct())
            .thenCompose(validation -> {
                if (!validation.isValid()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Product validation failed: " + 
                            String.join(", ", validation.getErrors()))
                    );
                }

                // Calculate price through plugin
                return plugin.calculatePrice(request.getProduct(), request.getPricingContext())
                    .thenCompose(priceCalc -> {
                        // Create schedule through plugin
                        return plugin.createSchedule(request.getProduct(), request.getScheduleRequest())
                            .thenCompose(schedule -> {
                                // Generate invoice through plugin
                                return plugin.generateInvoice(request.getProduct(), request.getInvoiceRequest())
                                    .thenCompose(invoice -> {
                                        // Process payment through plugin
                                        return plugin.processPayment(request.getProduct(), request.getPaymentRequest())
                                            .thenApply(paymentResult -> {
                                                // Build result
                                                return new BillingResult(
                                                    invoice,
                                                    paymentResult,
                                                    schedule,
                                                    priceCalc,
                                                    plugin.getPluginId(),
                                                    true,
                                                    "Billing processed successfully"
                                                );
                                            });
                                    });
                            });
                    });
            });
    }

    /**
     * Processes a refund for any product type.
     */
    public CompletionStage<RefundResult> processRefund(RefundRequest request) {
        BillingPlugin plugin = pluginRegistry.getPlugin(request.getPluginId());
        return plugin.processRefund(
            request.getProduct(),
            request
        );
    }

    /**
     * Cancels a billing schedule for any product type.
     */
    public CompletionStage<CancelResult> cancelSchedule(CancelRequest request) {
        BillingPlugin plugin = pluginRegistry.getPlugin(request.getPluginId());
        return plugin.cancelSchedule(
            request.getProduct(),
            request
        );
    }

    /**
     * Gets billing configuration for a product type.
     */
    public PluginConfigSchema getPluginConfig(ProductType productType) {
        BillingPlugin plugin = pluginRegistry.getDefaultPluginForProductType(productType);
        return plugin.getConfigSchema();
    }

    /**
     * Gets all available billing plugins.
     */
    public List<PluginMetadata> getAvailablePlugins() {
        return pluginRegistry.getAllPluginMetadata();
    }
}