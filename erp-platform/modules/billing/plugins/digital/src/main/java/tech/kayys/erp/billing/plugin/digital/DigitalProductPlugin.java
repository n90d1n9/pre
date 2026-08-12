package tech.kayys.erp.billing.plugin.digital;

import tech.kayys.erp.billing.plugin.BillingPlugin;
import tech.kayys.erp.billing.plugin.ProductType;
import tech.kayys.erp.billing.plugin.model.*;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Digital Product Billing Plugin.
 * Handles digital downloads, streaming, licenses, and software.
 */
@ApplicationScoped
public class DigitalProductPlugin implements BillingPlugin {

    @Override
    public String getPluginId() {
        return "digital-plugin-v1";
    }

    @Override
    public Set<ProductType> getSupportedProductTypes() {
        return Set.of(
            ProductType.DIGITAL_DOWNLOAD,
            ProductType.DIGITAL_STREAMING,
            ProductType.DIGITAL_LICENSE,
            ProductType.DIGITAL_SOFTWARE,
            ProductType.DIGITAL_GOODS
        );
    }

    @Override
    public PluginConfigSchema getConfigSchema() {
        return PluginConfigSchema.builder()
            .addField("licenseModel", "STRING", "PERPETUAL", "PERPETUAL", "SUBSCRIPTION", "RENTAL")
            .addField("maxActivations", "NUMBER", "1", "1", "10")
            .addField("downloadLimit", "NUMBER", "0", "0", "999")
            .addField("expiryDays", "NUMBER", "0", "0", "3650")
            .addField("requiresActivation", "BOOLEAN", "true")
            .addField("distributionMethod", "STRING", "DOWNLOAD", "DOWNLOAD", "STREAMING", "API")
            .build();
    }

    @Override
    public CompletionStage<ValidationResult> validateProduct(Product product) {
        try {
            // Validate digital product-specific attributes
            if (product.getLicenseModel() == null) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("License model is required for digital products")
                );
            }

            if (product.getPrice() == null || product.getPrice().isNegative()) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Price must be positive")
                );
            }

            if (product.getMaxActivations() < 1) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Max activations must be at least 1")
                );
            }

            return CompletableFuture.completedFuture(ValidationResult.success());
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                ValidationResult.failure(e.getMessage())
            );
        }
    }

    @Override
    public CompletionStage<PriceCalculation> calculatePrice(
            Product product,
            PricingContext context) {
        
        PriceCalculation calculation = new PriceCalculation();
        calculation.setProductId(product.getId());
        calculation.setCurrencyCode(product.getCurrencyCode());

        // Digital products typically have one-time pricing
        BigDecimal quantity = BigDecimal.valueOf(context.getQuantity());
        BigDecimal totalPrice = product.getPrice().getAmount().multiply(quantity);

        // Apply license model discount
        if ("SUBSCRIPTION".equals(product.getLicenseModel())) {
            // Subscription pricing
            totalPrice = product.getPrice().getAmount()
                .multiply(BigDecimal.valueOf(12))
                .multiply(BigDecimal.valueOf(0.9)); // 10% annual discount
        }

        calculation.setSubtotal(totalPrice);
        calculation.setTotal(totalPrice);
        calculation.setPricingDetails(Map.of(
            "productType", "DIGITAL",
            "licenseModel", product.getLicenseModel(),
            "maxActivations", product.getMaxActivations(),
            "quantity", context.getQuantity()
        ));

        return CompletableFuture.completedFuture(calculation);
    }

    @Override
    public CompletionStage<BillingSchedule> createSchedule(
            Product product,
            ScheduleRequest request) {
        
        BillingSchedule schedule = new BillingSchedule();
        schedule.setPluginId(getPluginId());
        schedule.setProductId(product.getId());
        schedule.setCustomerId(request.getCustomerId());
        schedule.setBillingModel("ONE_TIME");
        schedule.setFrequency("ONE_TIME");
        schedule.setNextBillingDate(null); // One-time billing
        
        // Set price
        schedule.setAmount(product.getPrice());
        schedule.setCurrencyCode(product.getCurrencyCode());
        
        // Set expiry if configured
        if (product.getExpiryDays() > 0) {
            schedule.setEndDate(Instant.now().plusSeconds(
                product.getExpiryDays() * 24L * 60L * 60L
            ));
        }

        return CompletableFuture.completedFuture(schedule);
    }

    @Override
    public CompletionStage<Invoice> generateInvoice(
            Product product,
            InvoiceRequest request) {
        
        Invoice invoice = new Invoice();
        invoice.setPluginId(getPluginId());
        invoice.setProductId(product.getId());
        invoice.setCustomerId(request.getCustomerId());
        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setInvoiceDate(Instant.now());
        
        // Calculate pricing
        PricingContext context = new PricingContext();
        context.setQuantity(request.getQuantity());
        
        PriceCalculation calculation = calculatePrice(product, context)
            .toCompletableFuture()
            .join();
        
        invoice.setAmount(calculation.getTotal());
        invoice.setCurrencyCode(product.getCurrencyCode());
        
        // Add digital product specific line items
        List<InvoiceLine> lines = new ArrayList<>();
        lines.add(new InvoiceLine(
            "Digital Product: " + product.getName(),
            calculation.getTotal(),
            product.getCurrencyCode(),
            1,
            calculation.getTotal()
        ));
        
        // Add license details if applicable
        if (product.getLicenseModel() != null) {
            lines.add(new InvoiceLine(
                "License: " + product.getLicenseModel(),
                BigDecimal.ZERO,
                product.getCurrencyCode(),
                1,
                BigDecimal.ZERO
            ));
        }
        
        invoice.setLines(lines);
        invoice.setDueDate(Instant.now().plusSeconds(14L * 24L * 60L * 60L)); // 14 days for digital
        
        return CompletableFuture.completedFuture(invoice);
    }

    @Override
    public CompletionStage<PaymentResult> processPayment(
            Product product,
            PaymentRequest request) {
        
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setTransactionId(UUID.randomUUID().toString());
        result.setPluginId(getPluginId());
        result.setProductId(product.getId());
        result.setAmount(request.getAmount());
        result.setCurrencyCode(request.getCurrencyCode());
        result.setProcessedAt(Instant.now());
        
        // Generate license key for digital products
        if (product.getRequiresActivation()) {
            result.setMetadata(Map.of(
                "licenseKey", generateLicenseKey(product),
                "downloadUrl", product.getDownloadUrl(),
                "maxActivations", String.valueOf(product.getMaxActivations())
            ));
        }
        
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<RefundResult> processRefund(
            Product product,
            RefundRequest request) {
        
        RefundResult result = new RefundResult();
        result.setSuccess(true);
        result.setRefundId(UUID.randomUUID().toString());
        result.setPluginId(getPluginId());
        result.setProductId(product.getId());
        result.setAmount(request.getAmount());
        result.setCurrencyCode(request.getCurrencyCode());
        result.setProcessedAt(Instant.now());
        
        // Revoke license on refund
        result.setMetadata(Map.of(
            "licenseRevoked", "true",
            "revocationReason", request.getReason()
        ));
        
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletionStage<CancelResult> cancelSchedule(
            Product product,
            CancelRequest request) {
        
        CancelResult result = new CancelResult();
        result.setSuccess(true);
        result.setScheduleId(request.getScheduleId());
        result.setPluginId(getPluginId());
        result.setProductId(product.getId());
        result.setCancelledAt(Instant.now());
        
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public PluginMetadata getMetadata() {
        return PluginMetadata.builder()
            .pluginId(getPluginId())
            .name("Digital Product Billing Plugin")
            .version("1.0.0")
            .description("Handles digital downloads, streaming, licenses, and software")
            .author("Kayys ERP")
            .supportedProductTypes(getSupportedProductTypes())
            .build();
    }

    private String generateInvoiceNumber() {
        return "DIG-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateLicenseKey(Product product) {
        // Generate a license key for digital products
        return UUID.randomUUID().toString()
            .replace("-", "")
            .substring(0, 16)
            .toUpperCase();
    }
}