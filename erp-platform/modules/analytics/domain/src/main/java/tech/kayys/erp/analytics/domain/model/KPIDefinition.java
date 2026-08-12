package tech.kayys.erp.analytics.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.analytics.domain.identifier.KPIId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * KPI definition aggregate root.
 * Defines a Key Performance Indicator and its targets.
 */
public final class KPIDefinition extends AggregateRoot<KPIId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String category;
    private String formula;
    private String unit;
    private double targetValue;
    private double minValue;
    private double maxValue;
    private String direction; // UP, DOWN, NEUTRAL
    private String frequency;
    private List<String> dataSources;
    private String owner;
    private boolean active;
    private String notes;

    private KPIDefinition(KPIId id) {
        super(id);
        this.dataSources = new ArrayList<>();
        this.active = true;
        this.direction = "UP";
    }

    private KPIDefinition() {
        super();
    }

    /**
     * Factory method to create a new KPI definition.
     */
    public static KPIDefinition create(
            KPIId id,
            String code,
            String name,
            String category,
            String formula,
            String unit,
            double targetValue,
            String owner) {
        KPIDefinition kpi = new KPIDefinition(id);
        kpi.code = code;
        kpi.name = name;
        kpi.category = category;
        kpi.formula = formula;
        kpi.unit = unit;
        kpi.targetValue = targetValue;
        kpi.owner = owner;
        return kpi;
    }

    /**
     * Adds a data source.
     */
    public void addDataSource(String dataSource) {
        if (!dataSources.contains(dataSource)) {
            dataSources.add(dataSource);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a data source.
     */
    public void removeDataSource(String dataSource) {
        dataSources.remove(dataSource);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the min and max values.
     */
    public void setRange(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the target.
     */
    public void updateTarget(double targetValue) {
        this.targetValue = targetValue;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the KPI information.
     */
    public void update(String name, String description, String formula) {
        this.name = name;
        this.description = description;
        this.formula = formula;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the KPI.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the KPI.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the direction.
     */
    public void setDirection(String direction) {
        this.direction = direction;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the frequency.
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getFormula() { return formula; }
    public String getUnit() { return unit; }
    public double getTargetValue() { return targetValue; }
    public double getMinValue() { return minValue; }
    public double getMaxValue() { return maxValue; }
    public String getDirection() { return direction; }
    public String getFrequency() { return frequency; }
    public List<String> getDataSources() { return Collections.unmodifiableList(dataSources); }
    public String getOwner() { return owner; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
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
        return "KPIDefinition{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", targetValue=" + targetValue +
                ", active=" + active +
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

    <module>modules/communication/domain</module>
    <module>modules/communication/application</module>
    <module>modules/communication/infrastructure</module>
    <module>modules/communication/interfaces</module>

    <module>modules/asset/domain</module>
    <module>modules/asset/application</module>
    <module>modules/asset/infrastructure</module>
    <module>modules/asset/interfaces</module>

    <module>modules/workforce/domain</module>
    <module>modules/workforce/application</module>
    <module>modules/workforce/infrastructure</module>
    <module>modules/workforce/interfaces</module>

    <module>modules/risk/domain</module>
    <module>modules/risk/application</module>
    <module>modules/risk/infrastructure</module>
    <module>modules/risk/interfaces</module>

    <module>modules/workflow/domain</module>
    <module>modules/workflow/application</module>
    <module>modules/workflow/infrastructure</module>
    <module>modules/workflow/interfaces</module>

    <module>modules/integration/domain</module>
    <module>modules/integration/application</module>
    <module>modules/integration/infrastructure</module>
    <module>modules/integration/interfaces</module>

    <module>modules/project/domain</module>
    <module>modules/project/application</module>
    <module>modules/project/infrastructure</module>
    <module>modules/project/interfaces</module>

    <module>modules/analytics/domain</module>
    <module>modules/analytics/application</module>
    <module>modules/analytics/infrastructure</module>
    <module>modules/analytics/interfaces</module>
</modules>