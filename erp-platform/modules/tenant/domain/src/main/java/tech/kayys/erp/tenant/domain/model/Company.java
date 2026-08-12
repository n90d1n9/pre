package tech.kayys.erp.tenant.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.tenant.domain.identifier.CompanyId;
import tech.kayys.erp.tenant.domain.identifier.TenantId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Company aggregate root.
 * Represents a company within a tenant.
 */
public final class Company extends AggregateRoot<CompanyId> {
    
    private static final long serialVersionUID = 1L;
    
    private TenantId tenantId;
    private String code;
    private String name;
    private String legalName;
    private String taxId;
    private String registrationNumber;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String phone;
    private String email;
    private String website;
    private String currencyCode;
    private String fiscalYearStart;
    private String industry;
    private String legalStructure;
    private String notes;
    private boolean active;
    private boolean defaultCompany;

    private Company(CompanyId id) {
        super(id);
        this.active = true;
    }

    private Company() {
        super();
    }

    /**
     * Factory method to create a new company.
     */
    public static Company create(
            CompanyId id,
            TenantId tenantId,
            String code,
            String name,
            String currencyCode) {
        Company company = new Company(id);
        company.tenantId = tenantId;
        company.code = code;
        company.name = name;
        company.currencyCode = currencyCode;
        return company;
    }

    /**
     * Updates company information.
     */
    public void update(String name, String legalName, String address, String city, String country) {
        this.name = name;
        this.legalName = legalName;
        this.address = address;
        this.city = city;
        this.country = country;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the tax information.
     */
    public void setTaxInfo(String taxId, String registrationNumber) {
        this.taxId = taxId;
        this.registrationNumber = registrationNumber;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the company.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the company.
     */
    public void deactivate() {
        if (defaultCompany) {
            throw new IllegalStateException("Cannot deactivate default company");
        }
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets as default company.
     */
    public void setAsDefault() {
        this.defaultCompany = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public TenantId getTenantId() { return tenantId; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getLegalName() { return legalName; }
    public String getTaxId() { return taxId; }
    public String getRegistrationNumber() { return registrationNumber; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWebsite() { return website; }
    public String getCurrencyCode() { return currencyCode; }
    public String getFiscalYearStart() { return fiscalYearStart; }
    public String getIndustry() { return industry; }
    public String getLegalStructure() { return legalStructure; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }
    public boolean isDefaultCompany() { return defaultCompany; }

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

    public void setPhone(String phone) {
        this.phone = phone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEmail(String email) {
        this.email = email;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setWebsite(String website) {
        this.website = website;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setFiscalYearStart(String fiscalYearStart) {
        this.fiscalYearStart = fiscalYearStart;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setIndustry(String industry) {
        this.industry = industry;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLegalStructure(String legalStructure) {
        this.legalStructure = legalStructure;
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
        return "Company{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}