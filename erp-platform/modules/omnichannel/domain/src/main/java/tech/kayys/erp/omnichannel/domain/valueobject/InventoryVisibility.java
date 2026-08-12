package tech.kayys.erp.omnichannel.domain.valueobject;

/**
 * Inventory visibility levels across channels.
 */
public enum InventoryVisibility {
    GLOBAL("Global - All channels can see"),
    CHANNEL_SPECIFIC("Channel Specific - Only certain channels"),
    STORE_ONLY("Store Only - Only physical store"),
    ONLINE_ONLY("Online Only - Only digital channels"),
    HIDDEN("Hidden - Not visible");

    private final String description;

    InventoryVisibility(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}