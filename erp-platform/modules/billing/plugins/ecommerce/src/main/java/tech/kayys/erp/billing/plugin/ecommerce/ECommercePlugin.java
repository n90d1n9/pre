package tech.kayys.erp.billing.plugin.ecommerce;

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
 * E-Commerce Billing Plugin.
 * Handles physical goods, digital goods, and subscription boxes.
 */
@ApplicationScoped
public class ECommercePlugin implements BillingPlugin {

    @Override
    public String getPluginId() {
        return "ecommerce-plugin-v1";
    }

    @Override
    public Set<ProductType> getSupportedProductTypes() {
        return Set.of(
            ProductType.PHYSICAL_GOODS,
            ProductType.DIGITAL_GOODS,
            ProductType.SUBSCRIPTION_BOX
        );
    }

    @Override
    public PluginConfigSchema getConfigSchema() {
        return PluginConfigSchema.builder()
            .addField("shippingRequired", "BOOLEAN", "true")
            .addField("taxable", "BOOLEAN", "true")
            .addField("weight", "NUMBER", "0.0")
            .addField("dimensions", "OBJECT", "{}")
            .addField("shippingClass", "STRING", "STANDARD", "STANDARD", "EXPEDITED", "OVERNIGHT")
            .addField("returnWindowDays", "NUMBER", "30", "0", "365")
            .addField("restockingFee", "NUMBER", "0", "0", "100")
            .build();
    }

    @Override
    public CompletionStage<ValidationResult> validateProduct(Product product) {
        try {
            // Validate e-commerce specific attributes
            if (product.getPrice() == null || product.getPrice().isNegative()) {
                return CompletableFuture.completedFuture(
                    ValidationResult.failure("Price must be positive")
                );
            }

            if (product.getProductType() == ProductType.PHYSICAL_GOODS) {
                if (product.getWeight() == null || product.getWeight() <= 0) {
                    return CompletableFuture.completedFuture(
                        ValidationResult.failure("Weight is required for physical goods")
                    );
                }
                if (product.getShippingClass() == null) {
                    return CompletableFuture.completedFuture(
                        ValidationResult.failure("Shipping class is required for physical goods")
                    );
                }
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

        BigDecimal quantity = BigDecimal.valueOf(context.getQuantity());
        BigDecimal totalPrice = product.getPrice().getAmount().multiply(quantity);

        // Apply shipping cost if physical
        if (product.getProductType() == ProductType.PHYSICAL_GOODS) {
            BigDecimal shippingCost = calculateShippingCost(product, context);
            totalPrice = totalPrice.add(shippingCost);
            calculation.setShippingCost(shippingCost);
        }

        // Apply tax if taxable
        if (product.isTaxable()) {
            BigDecimal taxRate = context.getTaxRate() != null ? 
                context.getTaxRate() : BigDecimal.valueOf(0.11); // 11% default
            BigDecimal taxAmount = totalPrice.multiply(taxRate);
            totalPrice = totalPrice.add(taxAmount);
            calculation.setTaxAmount(taxAmount);
            calculation.setTaxRate(taxRate);
        }

        // Apply subscription box discount
        if (product.getProductType() == ProductType.SUBSCRIPTION_BOX) {
            if ("ANNUAL".equals(context.getBillingCycle())) {
                totalPrice = totalPrice.multiply(BigDecimal.valueOf(12))
                    .multiply(BigDecimal.valueOf(0.9)); // 10% annual discount
            }
        }

        calculation.setSubtotal(totalPrice);
        calculation.setTotal(totalPrice);
        calculation.setPricingDetails(Map.of(
            "productType", product.getProductType().name(),
            "quantity", context.getQuantity(),
            "shippingClass", product.getShippingClass(),
            "isTaxable", product.isTaxable()
        ));

        return CompletableFuture.completedFuture(calculation);
    }

    private BigDecimal calculateShippingCost(Product product, PricingContext context) {
        // In production, integrate with shipping carrier APIs
        BigDecimal weight = BigDecimal.valueOf(product.getWeight());
        String shippingClass = product.getShippingClass();
        
        switch (shippingClass) {
            case "STANDARD":
                return weight.multiply(BigDecimal.valueOf(1.5));
            case "EXPEDITED":
                return weight.multiply(BigDecimal.valueOf(3.0));
            case "OVERNIGHT":
                return weight.multiply(BigDecimal.valueOf(6.0));
            default:
                return weight.multiply(BigDecimal.valueOf(2.0));
        }
    }

    @Override
    public CompletionStage<BillingSchedule> createSchedule(
            Product product,
            ScheduleRequest request) {
        
        BillingSchedule schedule = new BillingSchedule();
        schedule.setPluginId(getPluginId());
        schedule.setProductId(product.getId());
        schedule.setCustomerId(request.getCustomerId());
        
        if (product.getProductType() == ProductType.SUBSCRIPTION_BOX) {
            schedule.setBillingModel("SUBSCRIPTION");
            schedule.setFrequency(request.getBillingCycle() != null ? 
                request.getBillingCycle() : "MONTHLY");
            schedule.setNextBillingDate(Instant.now());
        } else {
            schedule.setBillingModel("ONE_TIME");
            schedule.setFrequency("ONE_TIME");
        }
        
        schedule.setAmount(product.getPrice());
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
        
        // Calculate pricing
        PricingContext context = new PricingContext();
        context.setQuantity(request.getQuantity());
        context.setBillingCycle(request.getBillingCycle());
        context.setTaxRate(request.getTaxRate());
        
        PriceCalculation calculation = calculatePrice(product, context)
            .toCompletableFuture()
            .join();
        
        invoice.setAmount(calculation.getTotal());
        invoice.setCurrencyCode(product.getCurrencyCode());
        
        // Build invoice lines
        List<InvoiceLine> lines = new ArrayList<>();
        
        // Product line
        lines.add(new InvoiceLine(
            product.getName(),
            product.getPrice().getAmount().multiply(BigDecimal.valueOf(request.getQuantity())),
            product.getCurrencyCode(),
            request.getQuantity(),
            product.getPrice().getAmount()
        ));
        
        // Shipping line
        if (calculation.getShippingCost() != null && !calculation.getShippingCost().isZero()) {
            lines.add(new InvoiceLine(
                "Shipping (" + product.getShippingClass() + ")",
                calculation.getShippingCost(),
                product.getCurrencyCode(),
                1,
                calculation.getShippingCost()
            ));
        }
        
        // Tax line
        if (calculation.getTaxAmount() != null && !calculation.getTaxAmount().isZero()) {
            lines.add(new InvoiceLine(
                "Tax (" + calculation.getTaxRate().multiply(BigDecimal.valueOf(100)) + "%)",
                calculation.getTaxAmount(),
                product.getCurrencyCode(),
                1,
                calculation.getTaxAmount()
            ));
        }
        
        invoice.setLines(lines);
        invoice.setDueDate(Instant.now().plusSeconds(30L * 24L * 60L * 60L)); // 30 days
        
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
        
        // Add shipping tracking info
        if (product.getProductType() == ProductType.PHYSICAL_GOODS) {
            result.setMetadata(Map.of(
                "shippingRequired", "true",
                "shippingClass", product.getShippingClass(),
                "weight", String.valueOf(product.getWeight())
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
        
        // Apply restocking fee if applicable
        if (product.getRestockingFee() > 0 && request.getQuantity() > 0) {
            BigDecimal restockingFee = request.getAmount().getAmount()
                .multiply(BigDecimal.valueOf(product.getRestockingFee() / 100.0));
            result.setRestockingFee(Money.of(restockingFee, request.getCurrencyCode()));
        }
        
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
            .name("E-Commerce Billing Plugin")
            .version("1.0.0")
            .description("Handles physical goods, digital goods, and subscription boxes")
            .author("Kayys ERP")
            .supportedProductTypes(getSupportedProductTypes())
            .build();
    }

    private String generateInvoiceNumber() {
        return "ECO-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8);
    }
}