package tech.kayys.syirkah.billing.pricing.strategy;

import tech.kayys.syirkah.billing.domain.plugin.PriceCalculation;
import tech.kayys.syirkah.billing.domain.valueobject.BillingItem;

import java.util.Map;

public interface PricingStrategy {

    PricingStrategyType getType();

    boolean supports(BillingItem item, Map<String, Object> parameters);

    PriceCalculation calculate(BillingItem item, Map<String, Object> parameters);
}
