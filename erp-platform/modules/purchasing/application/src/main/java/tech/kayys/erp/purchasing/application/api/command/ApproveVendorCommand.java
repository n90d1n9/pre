package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;

/**
 * Command to approve a vendor.
 */
public record ApproveVendorCommand(
        VendorId vendorId,
        String approvedBy
) implements Command<VendorId> {

    public ApproveVendorCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (approvedBy == null || approvedBy.trim().isEmpty()) {
            throw new IllegalArgumentException("Approved by is required");
        }
    }
}