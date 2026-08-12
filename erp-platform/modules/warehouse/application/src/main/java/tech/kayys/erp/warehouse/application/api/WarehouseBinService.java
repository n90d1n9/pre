package tech.kayys.erp.warehouse.application.api;

import tech.kayys.erp.warehouse.application.api.command.AssignProductToBinCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateBinLocationCommand;
import tech.kayys.erp.warehouse.application.api.query.BinLocationView;
import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Public API for warehouse bin operations.
 */
public interface WarehouseBinService {

    /**
     * Creates a new bin location.
     */
    CompletionStage<BinLocationId> createBinLocation(CreateBinLocationCommand command);

    /**
     * Gets a bin location by ID.
     */
    CompletionStage<BinLocationView> getBinLocation(BinLocationId binLocationId);

    /**
     * Gets bins in a warehouse with filters.
     */
    CompletionStage<List<BinLocationView>> getBins(
        UUID warehouseId, String zone, String type, String status, int page, int size
    );

    /**
     * Assigns a product to a bin.
     */
    CompletionStage<BinLocationId> assignProductToBin(AssignProductToBinCommand command);

    /**
     * Unassigns a product from a bin.
     */
    CompletionStage<BinLocationId> unassignProductFromBin(BinLocationId binId, String productId);

    /**
     * Gets available bins for a product.
     */
    CompletionStage<List<BinLocationView>> getAvailableBinsForProduct(
        String productId, int quantity
    );

    /**
     * Gets bins with low utilization.
     */
    CompletionStage<List<BinLocationView>> getBinsWithLowUtilization(
        double thresholdPercentage
    );
}