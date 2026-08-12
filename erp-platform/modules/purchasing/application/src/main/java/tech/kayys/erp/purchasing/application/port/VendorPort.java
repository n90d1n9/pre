package tech.kayys.erp.purchasing.application.port;

import tech.kayys.erp.purchasing.domain.identifier.VendorId;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Port for vendor information.
 */
public interface VendorPort {

    /**
     * Validates that a vendor exists and is active.
     */
    CompletionStage<Boolean> validateVendor(UUID vendorId);

    /**
     * Gets vendor details.
     */
    CompletionStage<VendorDetails> getVendorDetails(UUID vendorId);

    record VendorDetails(
        UUID vendorId,
        String name,
        String email,
        String phone,
        String address,
        String paymentTerms,
        String shippingTerms,
        String currencyCode,
        boolean active
    ) {}
}