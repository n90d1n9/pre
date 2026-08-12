package tech.kayys.erp.billing.plugin.registry;

import tech.kayys.erp.billing.plugin.BillingPlugin;
import tech.kayys.erp.billing.plugin.ProductType;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Plugin registry for billing plugins.
 * Discovers and manages all billing plugins.
 */
@ApplicationScoped
public class PluginRegistry {

    @Inject
    Instance<BillingPlugin> plugins;

    private Map<String, BillingPlugin> pluginMap;
    private Map<ProductType, List<BillingPlugin>> productTypePlugins;

    /**
     * Initializes the plugin registry.
     */
    public void initialize() {
        pluginMap = new HashMap<>();
        productTypePlugins = new HashMap<>();

        for (BillingPlugin plugin : plugins) {
            String pluginId = plugin.getPluginId();
            pluginMap.put(pluginId, plugin);

            for (ProductType productType : plugin.getSupportedProductTypes()) {
                productTypePlugins.computeIfAbsent(productType, k -> new ArrayList<>())
                    .add(plugin);
            }
        }
    }

    /**
     * Gets a plugin by ID.
     */
    public BillingPlugin getPlugin(String pluginId) {
        BillingPlugin plugin = pluginMap.get(pluginId);
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin not found: " + pluginId);
        }
        return plugin;
    }

    /**
     * Gets plugins for a product type.
     */
    public List<BillingPlugin> getPluginsForProductType(ProductType productType) {
        return productTypePlugins.getOrDefault(productType, List.of());
    }

    /**
     * Gets the default plugin for a product type.
     */
    public BillingPlugin getDefaultPluginForProductType(ProductType productType) {
        List<BillingPlugin> plugins = getPluginsForProductType(productType);
        if (plugins.isEmpty()) {
            throw new IllegalArgumentException("No plugin found for product type: " + productType);
        }
        return plugins.get(0);
    }

    /**
     * Gets all plugins.
     */
    public List<BillingPlugin> getAllPlugins() {
        return List.copyOf(pluginMap.values());
    }

    /**
     * Gets all plugin metadata.
     */
    public List<PluginMetadata> getAllPluginMetadata() {
        return pluginMap.values().stream()
            .map(BillingPlugin::getMetadata)
            .collect(Collectors.toList());
    }
}