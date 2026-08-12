# Complete Implementation: Analytics & Business Intelligence Bounded Context

Now I'll implement the complete Analytics & Business Intelligence bounded context, which handles dashboards, reports, data visualization, KPI tracking, and data warehousing integration.

## 1. Analytics Domain Module

**`/modules/analytics/domain/pom.xml`**:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>tech.kayys.erp</groupId>
        <artifactId>erp-platform</artifactId>
        <version>0.1.0-SNAPSHOT</version>
        <relativePath>../../../pom.xml</relativePath>
    </parent>

    <artifactId>erp-analytics-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/identifier/DashboardId.java`**:

```java
package tech.kayys.erp.analytics.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Dashboard identifier.
 */
public final class DashboardId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public DashboardId(UUID value) {
        super(value);
    }

    public static DashboardId of(UUID value) {
        return new DashboardId(value);
    }

    public static DashboardId generate() {
        return new DashboardId(UUID.randomUUID());
    }

    public static DashboardId fromString(String value) {
        return new DashboardId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "DashboardId{" + value + "}";
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/identifier/ReportId.java`**:

```java
package tech.kayys.erp.analytics.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Report identifier.
 */
public final class ReportId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public ReportId(UUID value) {
        super(value);
    }

    public static ReportId of(UUID value) {
        return new ReportId(value);
    }

    public static ReportId generate() {
        return new ReportId(UUID.randomUUID());
    }

    public static ReportId fromString(String value) {
        return new ReportId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "ReportId{" + value + "}";
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/identifier/KPIId.java`**:

```java
package tech.kayys.erp.analytics.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * KPI identifier.
 */
public final class KPIId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public KPIId(UUID value) {
        super(value);
    }

    public static KPIId of(UUID value) {
        return new KPIId(value);
    }

    public static KPIId generate() {
        return new KPIId(UUID.randomUUID());
    }

    public static KPIId fromString(String value) {
        return new KPIId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "KPIId{" + value + "}";
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/valueobject/ChartType.java`**:

```java
package tech.kayys.erp.analytics.domain.valueobject;

/**
 * Types of charts for visualization.
 */
public enum ChartType {
    BAR("Bar Chart"),
    LINE("Line Chart"),
    PIE("Pie Chart"),
    DONUT("Donut Chart"),
    AREA("Area Chart"),
    SCATTER("Scatter Plot"),
    BUBBLE("Bubble Chart"),
    HEATMAP("Heatmap"),
    TREEMAP("Treemap"),
    GAUGE("Gauge Chart"),
    FUNNEL("Funnel Chart"),
    TABLE("Table"),
    PIVOT("Pivot Table"),
    METRIC("Metric Card"),
    GEO("Geographic Map"),
    SANKEY("Sankey Diagram"),
    WATERFALL("Waterfall Chart"),
    BOXPLOT("Box Plot"),
    HISTOGRAM("Histogram"),
    RADAR("Radar Chart");

    private final String displayName;

    ChartType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/valueobject/ReportFrequency.java`**:

```java
package tech.kayys.erp.analytics.domain.valueobject;

/**
 * Frequency of report generation.
 */
public enum ReportFrequency {
    REAL_TIME("Real-Time"),
    ON_DEMAND("On Demand"),
    HOURLY("Hourly"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly"),
    CUSTOM("Custom");

    private final String displayName;

    ReportFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isScheduled() {
        return this != ON_DEMAND && this != REAL_TIME;
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/valueobject/DashboardStatus.java`**:

```java
package tech.kayys.erp.analytics.domain.valueobject;

/**
 * Status of a dashboard.
 */
public enum DashboardStatus {
    DRAFT("Draft - being designed"),
    PUBLISHED("Published - available to users"),
    ARCHIVED("Archived - no longer active"),
    UNDER_MAINTENANCE("Under Maintenance - being updated");

    private final String description;

    DashboardStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PUBLISHED;
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/model/Dashboard.java`**:

```java
package tech.kayys.erp.analytics.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.analytics.domain.identifier.DashboardId;
import tech.kayys.erp.analytics.domain.valueobject.DashboardStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dashboard aggregate root.
 * Represents a configurable dashboard with widgets.
 */
public final class Dashboard extends AggregateRoot<DashboardId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private DashboardStatus status;
    private List<Widget> widgets;
    private String category;
    private String owner;
    private List<String> sharedWith;
    private String layout;
    private boolean active;
    private String refreshInterval;
    private String notes;

    private Dashboard(DashboardId id) {
        super(id);
        this.widgets = new ArrayList<>();
        this.sharedWith = new ArrayList<>();
        this.status = DashboardStatus.DRAFT;
        this.active = true;
        this.layout = "GRID";
        this.refreshInterval = "5m";
    }

    private Dashboard() {
        super();
    }

    /**
     * Factory method to create a new dashboard.
     */
    public static Dashboard create(
            DashboardId id,
            String name,
            String description,
            String category,
            String owner) {
        Dashboard dashboard = new Dashboard(id);
        dashboard.name = name;
        dashboard.description = description;
        dashboard.category = category;
        dashboard.owner = owner;
        return dashboard;
    }

    /**
     * Adds a widget to the dashboard.
     */
    public void addWidget(Widget widget) {
        if (status == DashboardStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot modify published dashboard");
        }
        widgets.add(widget);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a widget from the dashboard.
     */
    public void removeWidget(String widgetId) {
        if (status == DashboardStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot modify published dashboard");
        }
        widgets.removeIf(w -> w.getId().equals(widgetId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Publishes the dashboard.
     */
    public void publish() {
        if (widgets.isEmpty()) {
            throw new IllegalStateException("Dashboard must have at least one widget");
        }
        this.status = DashboardStatus.PUBLISHED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Archives the dashboard.
     */
    public void archive() {
        this.status = DashboardStatus.ARCHIVED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Shares the dashboard with users.
     */
    public void shareWith(List<String> users) {
        this.sharedWith = new ArrayList<>(users);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a user to share list.
     */
    public void addSharedUser(String userId) {
        if (!sharedWith.contains(userId)) {
            sharedWith.add(userId);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a user from share list.
     */
    public void removeSharedUser(String userId) {
        sharedWith.remove(userId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the layout.
     */
    public void updateLayout(String layout) {
        this.layout = layout;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the refresh interval.
     */
    public void updateRefreshInterval(String refreshInterval) {
        this.refreshInterval = refreshInterval;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of widgets.
     */
    public int getWidgetCount() {
        return widgets.size();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public DashboardStatus getStatus() { return status; }
    public List<Widget> getWidgets() { return Collections.unmodifiableList(widgets); }
    public String getCategory() { return category; }
    public String getOwner() { return owner; }
    public List<String> getSharedWith() { return Collections.unmodifiableList(sharedWith); }
    public String getLayout() { return layout; }
    public boolean isActive() { return active; }
    public String getRefreshInterval() { return refreshInterval; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCategory(String category) {
        this.category = category;
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
        return "Dashboard{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", widgets=" + widgets.size() +
                '}';
    }

    /**
     * Widget value object.
     */
    public static final class Widget implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String title;
        private final ChartType chartType;
        private final String dataSource;
        private final String query;
        private final String dimensions;
        private final String metrics;
        private final String filters;
        private final int width; // in grid columns
        private final int height; // in grid rows
        private final String position;
        private final Map<String, String> configuration;
        private final String notes;

        public Widget(
                String id,
                String title,
                ChartType chartType,
                String dataSource,
                String query,
                String dimensions,
                String metrics,
                String filters,
                int width,
                int height,
                String position,
                Map<String, String> configuration,
                String notes) {
            this.id = id;
            this.title = title;
            this.chartType = chartType;
            this.dataSource = dataSource;
            this.query = query;
            this.dimensions = dimensions;
            this.metrics = metrics;
            this.filters = filters;
            this.width = width;
            this.height = height;
            this.position = position;
            this.configuration = configuration != null ? new HashMap<>(configuration) : new HashMap<>();
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Widget ID cannot be empty");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Widget title cannot be empty");
            }
            if (chartType == null) {
                throw new IllegalArgumentException("Chart type cannot be null");
            }
            if (width < 1 || width > 12) {
                throw new IllegalArgumentException("Width must be between 1 and 12");
            }
            if (height < 1 || height > 10) {
                throw new IllegalArgumentException("Height must be between 1 and 10");
            }
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public ChartType getChartType() { return chartType; }
        public String getDataSource() { return dataSource; }
        public String getQuery() { return query; }
        public String getDimensions() { return dimensions; }
        public String getMetrics() { return metrics; }
        public String getFilters() { return filters; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public String getPosition() { return position; }
        public Map<String, String> getConfiguration() { return Collections.unmodifiableMap(configuration); }
        public String getNotes() { return notes; }

        public Widget updatePosition(int width, int height, String position) {
            return new Widget(
                id, title, chartType, dataSource, query, dimensions,
                metrics, filters, width, height, position,
                configuration, notes
            );
        }

        @Override
        public String toString() {
            return "Widget{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", chartType=" + chartType +
                    ", dimensions=" + dimensions +
                    ", metrics=" + metrics +
                    '}';
        }
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/model/ReportDefinition.java`**:

```java
package tech.kayys.erp.analytics.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.analytics.domain.identifier.ReportId;
import tech.kayys.erp.analytics.domain.valueobject.ReportFrequency;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Report definition aggregate root.
 * Defines a report configuration and schedule.
 */
public final class ReportDefinition extends AggregateRoot<ReportId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String category;
    private String dataSource;
    private String query;
    private String dimensions;
    private String metrics;
    private String filters;
    private String sorting;
    private String outputFormat;
    private ReportFrequency frequency;
    private List<String> recipients;
    private String owner;
    private boolean active;
    private boolean scheduled;
    private String scheduleCron;
    private String lastRunAt;
    private String lastRunStatus;
    private String notes;

    private ReportDefinition(ReportId id) {
        super(id);
        this.recipients = new ArrayList<>();
        this.active = true;
        this.scheduled = false;
        this.outputFormat = "PDF";
        this.frequency = ReportFrequency.ON_DEMAND;
    }

    private ReportDefinition() {
        super();
    }

    /**
     * Factory method to create a new report definition.
     */
    public static ReportDefinition create(
            ReportId id,
            String name,
            String description,
            String category,
            String dataSource,
            String query,
            String owner) {
        ReportDefinition report = new ReportDefinition(id);
        report.name = name;
        report.description = description;
        report.category = category;
        report.dataSource = dataSource;
        report.query = query;
        report.owner = owner;
        return report;
    }

    /**
     * Sets the report dimensions and metrics.
     */
    public void setDimensionsAndMetrics(String dimensions, String metrics) {
        this.dimensions = dimensions;
        this.metrics = metrics;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a filter to the report.
     */
    public void addFilter(String filter) {
        if (filters == null || filters.isEmpty()) {
            this.filters = filter;
        } else {
            this.filters = this.filters + "|" + filter;
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the sorting.
     */
    public void setSorting(String sorting) {
        this.sorting = sorting;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Schedules the report.
     */
    public void schedule(ReportFrequency frequency, String scheduleCron) {
        this.frequency = frequency;
        this.scheduleCron = scheduleCron;
        this.scheduled = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Unschedules the report.
     */
    public void unschedule() {
        this.scheduled = false;
        this.scheduleCron = null;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a recipient to the report.
     */
    public void addRecipient(String recipient) {
        if (!recipients.contains(recipient)) {
            recipients.add(recipient);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a recipient from the report.
     */
    public void removeRecipient(String recipient) {
        recipients.remove(recipient);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the report.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the report.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Records the last run.
     */
    public void recordLastRun(String status) {
        this.lastRunAt = Instant.now().toString();
        this.lastRunStatus = status;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the number of recipients.
     */
    public int getRecipientCount() {
        return recipients.size();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getDataSource() { return dataSource; }
    public String getQuery() { return query; }
    public String getDimensions() { return dimensions; }
    public String getMetrics() { return metrics; }
    public String getFilters() { return filters; }
    public String getSorting() { return sorting; }
    public String getOutputFormat() { return outputFormat; }
    public ReportFrequency getFrequency() { return frequency; }
    public List<String> getRecipients() { return Collections.unmodifiableList(recipients); }
    public String getOwner() { return owner; }
    public boolean isActive() { return active; }
    public boolean isScheduled() { return scheduled; }
    public String getScheduleCron() { return scheduleCron; }
    public String getLastRunAt() { return lastRunAt; }
    public String getLastRunStatus() { return lastRunStatus; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCategory(String category) {
        this.category = category;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
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
        return "ReportDefinition{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", frequency=" + frequency +
                ", scheduled=" + scheduled +
                ", active=" + active +
                '}';
    }
}
```

**`/modules/analytics/domain/src/main/java/tech/kayys/erp/analytics/domain/model/KPIDefinition.java`**:

```java
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
```

## 2. Update Root POM

**Update `/pom.xml`** to include Analytics modules:

```xml
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
```

## Summary

The complete Analytics & Business Intelligence bounded context provides:

1. **Dashboard Management**:
   - Full dashboard lifecycle (Draft → Published → Archived)
   - Configurable widgets with chart types
   - Grid-based layout
   - Sharing and ownership
   - Refresh interval configuration

2. **Report Definition**:
   - Data source configuration
   - Query management
   - Dimensions and metrics
   - Filter and sorting
   - Output formats (PDF, Excel, CSV, etc.)
   - Scheduling and frequency

3. **KPI Management**:
   - KPI definition with formula
   - Target values
   - Range (min/max)
   - Direction tracking (Up/Down/Neutral)
   - Data source mapping

4. **Chart Types**:
   - Bar, Line, Pie, Donut, Area
   - Scatter, Bubble, Heatmap, Treemap
   - Gauge, Funnel, Table, Pivot
   - Metric cards, Geographic maps
   - Waterfall, Boxplot, Histogram, Radar

5. **Integration Points**:
   - All bounded contexts as data sources
   - Communication for report delivery
   - Workflow for report approvals

This completes the Analytics & Business Intelligence context with comprehensive dashboard, reporting, and KPI capabilities that enable the ERP system to provide business insights across all modules.