package tech.kayys.erp.omnichannel.application.api.query;

import java.util.List;

/**
 * Inventory visibility across channels view.
 */
public record InventoryVisibilityView(
        String productId,
        String productName,
        List<LocationInventory> locations,
        boolean availableOnline,
        boolean availableInStore,
        String nearestStore,
        int distanceMiles
) {

    public record LocationInventory(
            String locationId,
            String locationName,
            String locationType, // STORE, WAREHOUSE, DC
            int availableQuantity,
            int reservedQuantity,
            String availabilityStatus, // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
            boolean isVisible
    ) {}
}