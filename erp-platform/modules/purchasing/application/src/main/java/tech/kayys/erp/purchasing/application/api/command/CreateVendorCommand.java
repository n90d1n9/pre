package tech.kayys.erp.purchasing.application.api.command;

import tech.kayys.erp.foundation.application.Command;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import java.util.List;

/**
 * Command to create a new vendor.
 */
public record CreateVendorCommand(
        VendorId vendorId,
        String name,
        String legalName,
        String taxId,
        String email,
        String phone,
        String address,
        String city,
        String state,
        String postalCode,
        String country,
        String website,
        VendorType vendorType,
        String contactPerson,
        String contactEmail,
        String contactPhone,
        String paymentTerms,
        String shippingTerms,
        String currencyCode,
        String notes
) implements Command<VendorId> {

    public CreateVendorCommand {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Vendor name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (vendorType == null) {
            throw new IllegalArgumentException("Vendor type is required");
        }
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private VendorId vendorId;
        private String name;
        private String legalName;
        private String taxId;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String website;
        private VendorType vendorType;
        private String contactPerson;
        private String contactEmail;
        private String contactPhone;
        private String paymentTerms;
        private String shippingTerms;
        private String currencyCode = "USD";
        private String notes;

        public Builder vendorId(VendorId vendorId) {
            this.vendorId = vendorId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder legalName(String legalName) {
            this.legalName = legalName;
            return this;
        }

        public Builder taxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public Builder vendorType(VendorType vendorType) {
            this.vendorType = vendorType;
            return this;
        }

        public Builder contactPerson(String contactPerson) {
            this.contactPerson = contactPerson;
            return this;
        }

        public Builder contactEmail(String contactEmail) {
            this.contactEmail = contactEmail;
            return this;
        }

        public Builder contactPhone(String contactPhone) {
            this.contactPhone = contactPhone;
            return this;
        }

        public Builder paymentTerms(String paymentTerms) {
            this.paymentTerms = paymentTerms;
            return this;
        }

        public Builder shippingTerms(String shippingTerms) {
            this.shippingTerms = shippingTerms;
            return this;
        }

        public Builder currencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public CreateVendorCommand build() {
            if (vendorId == null) {
                vendorId = VendorId.generate();
            }
            return new CreateVendorCommand(
                vendorId, name, legalName, taxId, email, phone,
                address, city, state, postalCode, country,
                website, vendorType, contactPerson, contactEmail,
                contactPhone, paymentTerms, shippingTerms,
                currencyCode, notes
            );
        }
    }
}