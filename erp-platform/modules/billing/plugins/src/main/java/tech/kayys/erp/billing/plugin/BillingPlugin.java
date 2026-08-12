package tech.kayys.erp.billing.plugin;

import java.util.concurrent.CompletionStage;
import java.util.Set;

/**
 * Core interface for all billing plugins.
 * Each product type implements this interface to define its billing behavior.
 */
public interface BillingPlugin {

    /**
     * Gets the plugin identifier.
     */
    String getPluginId();

    /**
     * Gets the product types this plugin supports.
     */
    Set<ProductType> getSupportedProductTypes();

    /**
     * Gets the plugin configuration schema.
     */
    PluginConfigSchema getConfigSchema();

    /**
     * Validates if a product can be billed through this plugin.
     */
    CompletionStage<ValidationResult> validateProduct(Product product);

    /**
     * Calculates the price for a product.
     */
    CompletionStage<PriceCalculation> calculatePrice(
        Product product,
        PricingContext context
    );

    /**
     * Creates a billing schedule for a product.
     */
    CompletionStage<BillingSchedule> createSchedule(
        Product product,
        ScheduleRequest request
    );

    /**
     * Generates an invoice for a product.
     */
    CompletionStage<Invoice> generateInvoice(
        Product product,
        InvoiceRequest request
    );

    /**
     * Processes a payment for a product.
     */
    CompletionStage<PaymentResult> processPayment(
        Product product,
        PaymentRequest request
    );

    /**
     * Handles a refund for a product.
     */
    CompletionStage<RefundResult> processRefund(
        Product product,
        RefundRequest request
    );

    /**
     * Cancels a billing schedule.
     */
    CompletionStage<CancelResult> cancelSchedule(
        Product product,
        CancelRequest request
    );

    /**
     * Gets plugin-specific metadata.
     */
    PluginMetadata getMetadata();
}