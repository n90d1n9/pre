package tech.kayys.erp.workflow.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.workflow.domain.identifier.WorkflowDefinitionId;
import tech.kayys.erp.workflow.domain.valueobject.ApprovalType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Workflow definition aggregate root.
 * Defines a reusable workflow process.
 */
public final class WorkflowDefinition extends AggregateRoot<WorkflowDefinitionId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private String version;
    private List<WorkflowStep> steps;
    private List<String> triggers; // Event types that start this workflow
    private String category;
    private ApprovalType approvalType;
    private String createdBy;
    private String lastModifiedBy;
    private boolean active;
    private String notes;

    private WorkflowDefinition(WorkflowDefinitionId id) {
        super(id);
        this.steps = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.active = true;
        this.version = "1.0";
        this.approvalType = ApprovalType.SINGLE;
    }

    private WorkflowDefinition() {
        super();
    }

    /**
     * Factory method to create a new workflow definition.
     */
    public static WorkflowDefinition create(
            WorkflowDefinitionId id,
            String code,
            String name,
            String category,
            String createdBy) {
        WorkflowDefinition definition = new WorkflowDefinition(id);
        definition.code = code;
        definition.name = name;
        definition.category = category;
        definition.createdBy = createdBy;
        return definition;
    }

    /**
     * Adds a step to the workflow.
     */
    public void addStep(WorkflowStep step) {
        if (steps.stream().anyMatch(s -> s.getOrder().equals(step.getOrder()))) {
            throw new IllegalArgumentException("Step order already exists: " + step.getOrder());
        }
        steps.add(step);
        // Sort by order
        steps.sort((a, b) -> Integer.compare(a.getOrder(), b.getOrder()));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a step from the workflow.
     */
    public void removeStep(int order) {
        steps.removeIf(s -> s.getOrder() == order);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a trigger.
     */
    public void addTrigger(String trigger) {
        if (!triggers.contains(trigger)) {
            triggers.add(trigger);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a trigger.
     */
    public void removeTrigger(String trigger) {
        triggers.remove(trigger);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Sets the approval type.
     */
    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Increments the version.
     */
    public void incrementVersion() {
        String[] parts = version.split("\\.");
        if (parts.length == 2) {
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]) + 1;
            this.version = major + "." + minor;
        } else {
            this.version = "1.1";
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Activates the workflow definition.
     */
    public void activate() {
        if (steps.isEmpty()) {
            throw new IllegalStateException("Cannot activate workflow with no steps");
        }
        this.active = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Deactivates the workflow definition.
     */
    public void deactivate() {
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the step at a specific order.
     */
    public WorkflowStep getStepByOrder(int order) {
        return steps.stream()
            .filter(s -> s.getOrder() == order)
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the first step.
     */
    public WorkflowStep getFirstStep() {
        return steps.isEmpty() ? null : steps.get(0);
    }

    /**
     * Gets the next step after a given order.
     */
    public WorkflowStep getNextStep(int currentOrder) {
        return steps.stream()
            .filter(s -> s.getOrder() > currentOrder)
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets the step count.
     */
    public int getStepCount() {
        return steps.size();
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getVersion() { return version; }
    public List<WorkflowStep> getSteps() { return Collections.unmodifiableList(steps); }
    public List<String> getTriggers() { return Collections.unmodifiableList(triggers); }
    public String getCategory() { return category; }
    public ApprovalType getApprovalType() { return approvalType; }
    public String getCreatedBy() { return createdBy; }
    public String getLastModifiedBy() { return lastModifiedBy; }
    public boolean isActive() { return active; }
    public String getNotes() { return notes; }

    public void setDescription(String description) {
        this.description = description;
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
        return "WorkflowDefinition{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", steps=" + steps.size() +
                ", active=" + active +
                '}';
    }

    /**
     * Workflow step value object.
     */
    public static final class WorkflowStep implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final int order;
        private final String name;
        private final String description;
        private final String type; // TASK, APPROVAL, NOTIFICATION, CONDITION, SUB_PROCESS
        private final String role; // Required role to execute
        private final List<String> allowedUsers;
        private final int timeoutDays;
        private final boolean optional;
        private final String condition; // Expression for conditional steps
        private final String formTemplate;
        private final String action;

        public WorkflowStep(
                int order,
                String name,
                String description,
                String type,
                String role,
                List<String> allowedUsers,
                int timeoutDays,
                boolean optional,
                String condition,
                String formTemplate,
                String action) {
            this.order = order;
            this.name = name;
            this.description = description;
            this.type = type;
            this.role = role;
            this.allowedUsers = allowedUsers != null ? new ArrayList<>(allowedUsers) : new ArrayList<>();
            this.timeoutDays = timeoutDays;
            this.optional = optional;
            this.condition = condition;
            this.formTemplate = formTemplate;
            this.action = action;
            validate();
        }

        @Override
        public void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Step name cannot be empty");
            }
            if (type == null || type.trim().isEmpty()) {
                throw new IllegalArgumentException("Step type cannot be empty");
            }
            if (order < 0) {
                throw new IllegalArgumentException("Order cannot be negative");
            }
        }

        public int getOrder() { return order; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public String getRole() { return role; }
        public List<String> getAllowedUsers() { return Collections.unmodifiableList(allowedUsers); }
        public int getTimeoutDays() { return timeoutDays; }
        public boolean isOptional() { return optional; }
        public String getCondition() { return condition; }
        public String getFormTemplate() { return formTemplate; }
        public String getAction() { return action; }

        public boolean isApproval() {
            return "APPROVAL".equals(type);
        }

        public boolean isTask() {
            return "TASK".equals(type) || "APPROVAL".equals(type);
        }

        @Override
        public String toString() {
            return "WorkflowStep{" +
                    "order=" + order +
                    ", name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}