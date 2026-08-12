package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.VendorId;
import tech.kayys.erp.purchasing.domain.valueobject.VendorStatus;
import tech.kayys.erp.purchasing.domain.valueobject.VendorType;

import java.time.Instant;

/**
 * Vendor aggregate root.
 * Represents a supplier or vendor in the procurement system.
 */
public final class Vendor extends AggregateRoot<VendorId> {
    
    private static final long serialVersionUID = 1L;
    
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
    private VendorStatus status;
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String paymentTerms;
    private String shippingTerms;
    private String currencyCode;
    private String notes;
    private double rating;
    private int totalOrders;
    private int onTimeDeliveries;
    private int lateDeliveries;
    private boolean active;

    private Vendor(VendorId id) {
        super(id);
        this.status = VendorStatus.ACTIVE;
        this.active = true;
        this.rating = 0.0;
        this.totalOrders = 0;
        this.onTimeDeliveries = 0;
        this.lateDeliveries = 0;
    }

    private Vendor() {
        super();
    }

    /**
     * Factory method to create a new vendor.
     */
    public static Vendor create(
            VendorId id,
            String name,
            VendorType vendorType,
            String email,
            String currencyCode) {
        Vendor vendor = new Vendor(id);
        vendor.name = name;
        vendor.vendorType = vendorType;
        vendor.email = email;
        vendor.currencyCode = currencyCode;
        vendor.status = VendorStatus.PENDING_APPROVAL;
        return vendor;
    }

    /**
     * Approves the vendor.
     */
    public void approve() {
        if (status != VendorStatus.PENDING_APPROVAL && status != VendorStatus.UNDER_REVIEW) {
            throw new IllegalStateException("Cannot approve vendor in status: " + status);
        }
        this.status = VendorStatus.ACTIVE;
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the vendor.
     */
    public void activate() {
        if (status == VendorStatus.BLACKLISTED) {
            throw new IllegalStateException("Cannot activate blacklisted vendor");
        }
        this.active = true;
        this.status = VendorStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the vendor.
     */
    public void deactivate() {
        this.active = false;
        this.status = VendorStatus.INACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Blacklists the vendor.
     */
    public void blacklist(String reason) {
        this.status = VendorStatus.BLACKLISTED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records a delivery for performance tracking.
     */
    public void recordDelivery(boolean onTime) {
        this.totalOrders++;
        if (onTime) {
            this.onTimeDeliveries++;
        } else {
            this.lateDeliveries++;
        }
        this.rating = calculateRating();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    private double calculateRating() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) onTimeDeliveries / totalOrders * 5.0;
    }

    /**
     * Gets the vendor's performance score.
     */
    public double getPerformanceScore() {
        if (totalOrders == 0) {
            return 0.0;
        }
        return (double) onTimeDeliveries / totalOrders * 100.0;
    }

    // Getters
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getTaxId() { return taxId; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getWebsite() { return website; }
    public VendorType getVendorType() { return vendorType; }
    public VendorStatus getStatus() { return status; }
    public String getContactPerson() { return contactPerson; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public String getPaymentTerms() { return paymentTerms; }
    public String getShippingTerms() { return shippingTerms; }
    public String getCurrencyCode() { return currencyCode; }
    public String getNotes() { return notes; }
    public double getRating() { return rating; }
    public int getTotalOrders() { return totalOrders; }
    public int getOnTimeDeliveries() { return onTimeDeliveries; }
    public int getLateDeliveries() { return lateDeliveries; }
    public boolean isActive() { return active && status == VendorStatus.ACTIVE; }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAddress(String address) {
        this.address = address;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCity(String city) {
        this.city = city;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setState(String state) {
        this.state = state;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCountry(String country) {
        this.country = country;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setWebsite(String website) {
        this.website = website;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setShippingTerms(String shippingTerms) {
        this.shippingTerms = shippingTerms;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", vendorType=" + vendorType +
                ", status=" + status +
                ", rating=" + rating +
                '}';
    }
}