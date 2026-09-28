package tech.kayys.syirkah.billing.domain.plugin;

import tech.kayys.syirkah.foundation.domain.valueobject.Money;
import java.util.Map;

public record PriceCalculation(
    Money subtotal,
    Money taxAmount,
    Money discountAmount,
    Money totalAmount,
    Map<String, Object> calculationDetails
) {}
