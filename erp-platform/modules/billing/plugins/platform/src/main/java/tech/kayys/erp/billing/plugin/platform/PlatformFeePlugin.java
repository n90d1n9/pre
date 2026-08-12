package tech.kayys.erp.billing.plugin.platform;

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
 * Platform Fee Billing Plugin.
 * Handles platform fees, marketplace fees, and commissions.
 */
@ApplicationScoped
public class PlatformFeePlugin implements BillingPlugin {

    @Override
    public String getPluginId() {
        return "platform-plugin-v1";
    }

    @Override
    public Set<ProductType> getSupportedProductTypes() {
        return Set.of(
            ProductType.PLATFORM_FEE,
            ProductType.MARKETPLACE_FEE,
            ProductType.COMMISSION
        );
    }

    @Override
    public PluginConfigSchema getConfigSchema() {
        return PluginConfigSchema.builder()
            .addField("feeType", "STRING", "FIXED", "FIXED", "PERCENTAGE", "TIERED")
            .addField("feeValue", "NUMBER", "0.0")
            .addField("minimumFee", "NUMBER", "0.0")
            .addField("maximumFee", "NUMBER", "0.0")
            .addField("calculationBasis", "STRING", "TRANSACTION_AMOUNT", "TRANSACTION_AMOUNT", "ORDER_COUNT")
            .addField("vendorPays", "BOOLEAN", "true")
            .addField("customerPays", "BOOLEAN", "false")
            .build();
    }

    @Override
    public CompletionStage<ValidationResult> validateProduct(Product product) {
        try {
            if (product.getFeeType() == null) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Fee type is required for platform products")
                );
            }

            if (product.getFeeValue() == null || product.getFeeValue().signum() < 0) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Fee value must be positive")
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

        BigDecimal feeAmount = BigDecimal.ZERO;
        BigDecimal transactionAmount = context.getTransactionAmount() != null ? 
            context.getTransactionAmount() : BigDecimal.ZERO;

        switch (product.getFeeType()) {
            case "FIXED":
                feeAmount = product.getFeeValue();
                break;
            case "PERCENTAGE":
                feeAmount = transactionAmount.multiply(
                    product.getFeeValue().divide(BigDecimal.valueOf(100))
                );
                break;
            case "TIERED":
                feeAmount = calculateTieredFee(product, context);
                break;
        }

        // Apply minimum fee
        if (product.getMinimumFee() != null && feeAmount.compareTo(product.getMinimumFee()) < 0) {
            feeAmount = product.getMinimumFee();
        }

        // Apply maximum fee
        if (product.getMaximumFee() != null && feeAmount.compareTo(product.getMaximumFee()) > 0) {
            feeAmount = product.getMaximumFee();
        }

        calculation.setSubtotal(feeAmount);
        calculation.setTotal(feeAmount);
        calculation.setPricingDetails(Map.of(
            "feeType", product.getFeeType(),
            "feeValue", product.getFeeValue(),
            "transactionAmount", transactionAmount,
            "minimumFee", product.getMinimumFee(),
            "maximumFee", product.getMaximumFee()
        ));

        return CompletableFuture.completedFuture(calculation);
    }

    private BigDecimal calculateTieredFee(Product product, PricingContext context) {
        BigDecimal transactionAmount = context.getTransactionAmount();
        BigDecimal feeAmount = BigDecimal.ZERO;

        // Sort tiers by threshold
        List<FeeTier> sortedTiers = product.getFeeTiers().stream()
            .sorted(Comparator.comparing(FeeTier::getThreshold))
            .collect(Collectors.toList());

        for (FeeTier tier : sortedTiers) {
            if (transactionAmount.compareTo(tier.getThreshold()) >= 0) {
                feeAmount = transactionAmount.multiply(
                    tier.getRate().divide(BigDecimal.valueOf(100))
                );
            }
        }

        return feeAmount;
    }

    @Override
    public CompletionStage<BillingSchedule> createSchedule(
            Product product,
            ScheduleRequest request) {
        
        BillingSchedule schedule = new BillingSchedule();
        schedule.setPluginId(getPluginId());
        schedule.setProductId(product.getId());
        schedule.setCustomerId(request.getCustomerId());
        schedule.setBillingModel("FEE");
        schedule.setFrequency("PER_TRANSACTION");
        
        // Calculate fee based on transaction
        PricingContext context = new PricingContext();
        context.setTransactionAmount(request.getTransactionAmount());
        
        PriceCalculation calculation = calculatePrice(product, context)
            .toCompletableFuture()
            .join();
        
        schedule.setAmount(Money.of(calculation.getTotal(), product.getCurrencyCode()));
        schedule.setCurrencyCode(product.getCurrencyCode());
        
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
        
        // Calculate fee
        PricingContext context = new PricingContext();
        context.setTransactionAmount(request.getTransactionAmount());
        
        PriceCalculation calculation = calculatePrice(product, context)
            .toCompletableFuture()
            .join();
        
        invoice.setAmount(calculation.getTotal());
        invoice.setCurrencyCode(product.getCurrencyCode());
        
        // Build invoice lines
        List<InvoiceLine> lines = new ArrayList<>();
        lines.add(new InvoiceLine(
            product.getName() + " Fee",
            calculation.getTotal(),
            product.getCurrencyCode(),
            1,
            calculation.getTotal()
        ));
        
        if (product.getFeeType().equals("PERCENTAGE")) {
            lines.add(new InvoiceLine(
                "Based on: " + context.getTransactionAmount() + " at " + 
                product.getFeeValue() + "%",
                BigDecimal.ZERO,
                product.getCurrencyCode(),
                1,
                BigDecimal.ZERO
            ));
        }
        
        invoice.setLines(lines);
        invoice.setDueDate(Instant.now().plusSeconds(14L * 24L * 60L * 60L)); // 14 days
        
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
        
        // Determine payer
        String payer = product.isVendorPays() ? "VENDOR" : "CUSTOMER";
        result.setMetadata(Map.of(
            "payer", payer,
            "feeType", product.getFeeType()
        ));
        
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
            .name("Platform Fee Billing Plugin")
            .version("1.0.0")
            .description("Handles platform fees, marketplace fees, and commissions")
            .author("Kayys ERP")
            .supportedProductTypes(getSupportedProductTypes())
            .build();
    }

    private String generateInvoiceNumber() {
        return "PLF-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8);
    }
}