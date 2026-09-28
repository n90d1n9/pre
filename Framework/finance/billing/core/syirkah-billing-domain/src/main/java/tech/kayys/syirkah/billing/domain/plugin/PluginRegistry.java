package tech.kayys.syirkah.billing.domain.plugin;

import tech.kayys.syirkah.billing.domain.valueobject.BillingContext;

import java.util.Optional;

public interface PluginRegistry {

    void register(BillingPlugin plugin);

    Optional<BillingPlugin> findPlugin(ProductType productType);

    Optional<BillingPlugin> findPluginForContext(BillingContext context);
}
