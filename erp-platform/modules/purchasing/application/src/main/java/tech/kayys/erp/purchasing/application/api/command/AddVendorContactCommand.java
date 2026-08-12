package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;

/**
 * Command to add a contact to a vendor.
 */
public record AddVendorContactCommand(
        VendorId vendorId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String mobile,
        String jobTitle,
        String department,
        boolean primary
) implements Command<VendorId> {

    public AddVendorContactCommand {
        if (vendorId == null) {
            throw new IllegalArgumentException("Vendor ID cannot be null");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
    }
}