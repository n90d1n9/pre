package tech.kayys.erp.communication.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.communication.domain.identifier.TemplateId;
import tech.kayys.erp.communication.domain.valueobject.MessageType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Communication template aggregate root.
 * Represents a reusable communication template.
 */
public final class Template extends AggregateRoot<TemplateId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private MessageType type;
    private String subject;
    private String body;
    private String htmlBody;
    private String rawContent;
    private List<String> tags;
    private Map<String, String> defaultVariables;
    private String category;
    private String language;
    private String version;
    private boolean active;
    private String createdBy;
    private String lastModifiedBy;
    private String notes;

    private Template(TemplateId id) {
        super(id);
        this.tags = new ArrayList<>();
        this.active = true;
        this.version = "1.0";
        this.language = "en";
    }

    private Template() {
        super();
    }

    /**
     * Factory method to create a new template.
     */
    public static Template create(
            TemplateId id,
            String name,
            MessageType type,
            String subject,
            String body) {
        Template template = new Template(id);
        template.name = name;
        template.type = type;
        template.subject = subject;
        template.body = body;
        return template;
    }

    /**
     * Updates the template content.
     */
    public void updateContent(String subject, String body, String htmlBody) {
        this.subject = subject;
        this.body = body;
        this.htmlBody = htmlBody;
        this.version = incrementVersion(this.version);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the HTML body.
     */
    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a tag to the template.
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a tag from the template.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets default variables.
     */
    public void setDefaultVariables(Map<String, String> defaultVariables) {
        this.defaultVariables = defaultVariables;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Renders the template with variables.
     */
    public String render(Map<String, String> variables) {
        String rendered = body;
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
        }
        return rendered;
    }

    private String incrementVersion(String version) {
        String[] parts = version.split("\\.");
        if (parts.length == 2) {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]) + 1;
            return major + "." + minor;
        }
        return "1.1";
    }

    /**
     * Activates the template.
     */
    public void activate() {
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the template.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public MessageType getType() { return type; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getHtmlBody() { return htmlBody; }
    public String getRawContent() { return rawContent; }
    public List<String> getTags() { return Collections.unmodifiableList(tags); }
    public Map<String, String> getDefaultVariables() { return defaultVariables; }
    public String getCategory() { return category; }
    public String getLanguage() { return language; }
    public String getVersion() { return version; }
    public boolean isActive() { return active; }
    public String getCreatedBy() { return createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCategory(String category) {
        this.category = category;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLanguage(String language) {
        this.language = language;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
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
        return "Template{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", version='" + version + '\'' +
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
</modules>