package tech.kayys.syirkah.billing.domain.plugin;

import tech.kayys.syirkah.billing.domain.model.Invoice;
import tech.kayys.syirkah.billing.domain.valueobject.BillingContext;

import java.util.Set;
import java.util.concurrent.CompletionStage;

/**
 * Universal plug-and-play SPI for industry billing extensions (SaaS, Retail, FnB, Grocery).
 */
public interface BillingPlugin {

    String getPluginId();

    Set<ProductType> getSupportedProductTypes();

    boolean canHandle(BillingContext context);

    CompletionStage<PriceCalculation> calculatePrice(PricingContext context);

    CompletionStage<Invoice> enrichInvoice(Invoice invoice, BillingContext context);
}
