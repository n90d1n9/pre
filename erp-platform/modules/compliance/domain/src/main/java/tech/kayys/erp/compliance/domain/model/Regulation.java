package tech.kayys.erp.compliance.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.compliance.domain.identifier.RegulationId;
import tech.kayys.erp.compliance.domain.valueobject.RegulationType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Regulation aggregate root.
 * Represents a regulatory framework that imposes compliance requirements.
 */
public final class Regulation extends AggregateRoot<RegulationId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private RegulationType regulationType;
    private String jurisdiction;
    private String effectiveDate;
    private String expirationDate;
    private List<String> applicableIndustries;
    private List<String> applicableRegions;
    private String requirementsSummary;
    private String penalties;
    private List<String> documentationUrls;
    private String notes;
    private boolean active;

    private Regulation(RegulationId id) {
        super(id);
        this.applicableIndustries = new ArrayList<>();
        this.applicableRegions = new ArrayList<>();
        this.documentationUrls = new ArrayList<>();
        this.active = true;
    }

    private Regulation() {
        super();
    }

    /**
     * Factory method to create a new regulation.
     */
    public static Regulation create(
            RegulationId id,
            String name,
            RegulationType regulationType,
            String jurisdiction) {
        Regulation regulation = new Regulation(id);
        regulation.name = name;
        regulation.regulationType = regulationType;
        regulation.jurisdiction = jurisdiction;
        return regulation;
    }

    /**
     * Adds an applicable industry.
     */
    public void addApplicableIndustry(String industry) {
        if (!applicableIndustries.contains(industry)) {
            applicableIndustries.add(industry);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Adds an applicable region.
     */
    public void addApplicableRegion(String region) {
        if (!applicableRegions.contains(region)) {
            applicableRegions.add(region);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Adds a documentation URL.
     */
    public void addDocumentationUrl(String url) {
        if (!documentationUrls.contains(url)) {
            documentationUrls.add(url);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Activates the regulation.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the regulation.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public RegulationType getRegulationType() { return regulationType; }
    public String getJurisdiction() { return jurisdiction; }
    public String getEffectiveDate() { return effectiveDate; }
    public String getExpirationDate() { return expirationDate; }
    public List<String> getApplicableIndustries() { return Collections.unmodifiableList(applicableIndustries); }
    public List<String> getApplicableRegions() { return Collections.unmodifiableList(applicableRegions); }
    public String getRequirementsSummary() { return requirementsSummary; }
    public String getPenalties() { return penalties; }
    public List<String> getDocumentationUrls() { return Collections.unmodifiableList(documentationUrls); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRequirementsSummary(String requirementsSummary) {
        this.requirementsSummary = requirementsSummary;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPenalties(String penalties) {
        this.penalties = penalties;
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
        return "Regulation{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", regulationType=" + regulationType +
                ", jurisdiction='" + jurisdiction + '\'' +
                '}';
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/employee/domain</module>
    <module>modules/employee/application</module>
    <module>modules/employee/infrastructure</module>
    <module>modules/employee/interfaces</module>

    <module>modules/payroll/domain</module>
    <module>modules/payroll/application</module>
    <module>modules/payroll/infrastructure</module>
    <module>modules/payroll/interfaces</module>

    <module>modules/hris/domain</module>
    <module>modules/hris/application</module>
    <module>modules/hris/infrastructure</module>
    <module>modules/hris/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/stockopname/domain</module>
    <module>modules/stockopname/application</module>
    <module>modules/stockopname/infrastructure</module>
    <module>modules/stockopname/interfaces</module>

    <module>modules/warehouse/domain</module>
    <module>modules/warehouse/application</module>
    <module>modules/warehouse/infrastructure</module>
    <module>modules/warehouse/interfaces</module>

    <module>modules/crm/domain</module>
    <module>modules/crm/application</module>
    <module>modules/crm/infrastructure</module>
    <module>modules/crm/interfaces</module>

    <module>modules/tenant/domain</module>
    <module>modules/tenant/application</module>
    <module>modules/tenant/infrastructure</module>
    <module>modules/tenant/interfaces</module>

    <module>modules/compliance/domain</module>
    <module>modules/compliance/application</module>
    <module>modules/compliance/infrastructure</module>
    <module>modules/compliance/interfaces</module>
</modules>