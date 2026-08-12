package tech.kayys.erp.billing.plugin.saas;

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
 * SaaS Billing Plugin.
 * Handles SaaS subscriptions, usage-based billing, and tiered pricing.
 */
@ApplicationScoped
public class SaaSPlugin implements BillingPlugin {

    @Override
    public String getPluginId() {
        return "saas-plugin-v1";
    }

    @Override
    public Set<ProductType> getSupportedProductTypes() {
        return Set.of(
            ProductType.SAAS_SUBSCRIPTION,
            ProductType.SAAS_USAGE_BASED,
            ProductType.SAAS_TIERED
        );
    }

    @Override
    public PluginConfigSchema getConfigSchema() {
        return PluginConfigSchema.builder()
            .addField("billingModel", "SUBSCRIPTION", "SUBSCRIPTION", "USAGE_BASED", "TIERED")
            .addField("trialPeriodDays", "NUMBER", "0", "0", "365")
            .addField("gracePeriodDays", "NUMBER", "5", "0", "30")
            .addField("prorationEnabled", "BOOLEAN", "true")
            .addField("usageMeterIds", "ARRAY", "[]")
            .addField("tierConfigs", "OBJECT", "{}")
            .build();
    }

    @Override
    public CompletionStage<ValidationResult> validateProduct(Product product) {
        try {
            // Validate SaaS-specific product attributes
            if (product.getBillingModel() == null) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Billing model is required for SaaS products")
                );
            }

            if (product.getPrice() == null || product.getPrice().isNegative()) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Price must be positive")
                );
            }

            // Validate tier configuration if tiered
            if (product.getBillingModel() == "TIERED" && product.getTiers().isEmpty()) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Tiers are required for tiered pricing")
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

        switch (product.getBillingModel()) {
            case "SUBSCRIPTION":
                calculateSubscriptionPrice(product, context, calculation);
                break;
            case "USAGE_BASED":
                calculateUsageBasedPrice(product, context, calculation);
                break;
            case "TIERED":
                calculateTieredPrice(product, context, calculation);
                break;
            default:
                calculation.setError("Unsupported billing model: " + product.getBillingModel());
        }

        return CompletableFuture.completedFuture(calculation);
    }

    private void calculateSubscriptionPrice(
            Product product,
            PricingContext context,
            PriceCalculation calculation) {
        
        BigDecimal monthlyPrice = product.getPrice().getAmount();
        BigDecimal quantity = BigDecimal.valueOf(context.getQuantity());
        
        // Apply volume discount if applicable
        BigDecimal totalPrice = monthlyPrice.multiply(quantity);
        
        // Apply discount based on billing cycle
        if ("ANNUAL".equals(context.getBillingCycle())) {
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(12))
                .multiply(BigDecimal.valueOf(0.85)); // 15% annual discount
        } else if ("QUARTERLY".equals(context.getBillingCycle())) {
            totalPrice = totalPrice.multiply(BigDecimal.valueOf(3))
                .multiply(BigDecimal.valueOf(0.95)); // 5% quarterly discount
        }

        calculation.setSubtotal(totalPrice);
        calculation.setTotal(totalPrice);
        calculation.setPricingDetails(Map.of(
            "billingModel", "SUBSCRIPTION",
            "quantity", context.getQuantity(),
            "billingCycle", context.getBillingCycle()
        ));
    }

    private void calculateUsageBasedPrice(
            Product product,
            PricingContext context,
            PriceCalculation calculation) {
        
        // Get usage data from context
        Map<String, Double> usage = context.getUsageData();
        double totalCost = 0.0;

        for (Map.Entry<String, Double> entry : usage.entrySet()) {
            String meterId = entry.getKey();
            double usageQuantity = entry.getValue();
            
            // Get price per unit from product configuration
            BigDecimal pricePerUnit = product.getUsagePrices().get(meterId);
            if (pricePerUnit != null) {
                totalCost += pricePerUnit.doubleValue() * usageQuantity;
            }
        }

        calculation.setSubtotal(BigDecimal.valueOf(totalCost));
        calculation.setTotal(BigDecimal.valueOf(totalCost));
        calculation.setPricingDetails(Map.of(
            "billingModel", "USAGE_BASED",
            "usageData", usage
        ));
    }

    private void calculateTieredPrice(
            Product product,
            PricingContext context,
            PriceCalculation calculation) {
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<TierDetail> tierDetails = new ArrayList<>();
        int remainingUnits = context.getQuantity();

        for (ProductTier tier : product.getTiers()) {
            if (remainingUnits <= 0) break;
            
            int unitsInTier = Math.min(
                remainingUnits, 
                tier.getMaxUnits() - tier.getMinUnits() + 1
            );
            
            BigDecimal tierPrice = tier.getPrice()
                .multiply(BigDecimal.valueOf(unitsInTier));
            
            totalPrice = totalPrice.add(tierPrice);
            remainingUnits -= unitsInTier;
            
            tierDetails.add(new TierDetail(
                tier.getName(),
                unitsInTier,
                tierPrice
            ));
        }

        calculation.setSubtotal(totalPrice);
        calculation.setTotal(totalPrice);
        calculation.setTierDetails(tierDetails);
        calculation.setPricingDetails(Map.of(
            "billingModel", "TIERED",
            "tiers", tierDetails
        ));
    }

    @Override
    public CompletionStage<BillingSchedule> createSchedule(
            Product product,
            ScheduleRequest request) {
        
        BillingSchedule schedule = new BillingSchedule();
        schedule.setPluginId(getPluginId());
        schedule.setProductId(product.getId());
        schedule.setCustomerId(request.getCustomerId());
        schedule.setBillingModel(product.getBillingModel());
        
        // Set billing frequency based on product type
        if (product.getBillingModel().equals("SUBSCRIPTION")) {
            schedule.setFrequency(request.getBillingCycle() != null ? 
                request.getBillingCycle() : "MONTHLY");
            schedule.setNextBillingDate(Instant.now());
        } else if (product.getBillingModel().equals("USAGE_BASED")) {
            schedule.setFrequency("USAGE");
            schedule.setNextBillingDate(Instant.now().plusSeconds(30L * 24L * 60L * 60L));
        }

        // Set price
        schedule.setAmount(product.getPrice());
        schedule.setCurrencyCode(product.getCurrencyCode());
        
        // Set trial period if configured
        if (product.getTrialPeriodDays() > 0) {
            schedule.setTrialEndDate(
                Instant.now().plusSeconds(
                    product.getTrialPeriodDays() * 24L * 60L * 60L
                )
            );
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
        context.setBillingCycle(request.getBillingCycle());
        context.setUsageData(request.getUsageData());
        
        PriceCalculation calculation = calculatePrice(product, context)
            .toCompletableFuture()
            .join();
        
        invoice.setAmount(calculation.getTotal());
        invoice.setCurrencyCode(product.getCurrencyCode());
        invoice.setLines(calculation.getLineItems());
        
        // Set due date (30 days)
        invoice.setDueDate(Instant.now().plusSeconds(30L * 24L * 60L * 60L));
        
        return CompletableFuture.completedFuture(invoice);
    }

    @Override
    public CompletionStage<PaymentResult> processPayment(
            Product product,
            PaymentRequest request) {
        
        // Delegate to payment gateway adapter
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setTransactionId(UUID.randomUUID().toString());
        result.setPluginId(getPluginId());
        result.setProductId(product.getId());
        result.setAmount(request.getAmount());
        result.setCurrencyCode(request.getCurrencyCode());
        result.setProcessedAt(Instant.now());
        
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
        result.setProratedRefund(request.isProratedRefund());
        
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public PluginMetadata getMetadata() {
        return PluginMetadata.builder()
            .pluginId(getPluginId())
            .name("SaaS Billing Plugin")
            .version("1.0.0")
            .description("Handles SaaS subscriptions, usage-based, and tiered pricing")
            .author("Kayys ERP")
            .supportedProductTypes(getSupportedProductTypes())
            .build();
    }

    private String generateInvoiceNumber() {
        return "INV-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8);
    }
}