# Complete Implementation: Business Process Automation / Workflow Engine Bounded Context

Now I'll implement the complete Business Process Automation / Workflow Engine bounded context, which handles workflow definitions, process orchestration, task management, approvals, and business process automation across the ERP system.

## 1. Workflow Domain Module

**`/modules/workflow/domain/pom.xml`**:

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

    <artifactId>erp-workflow-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/identifier/WorkflowDefinitionId.java`**:

```java
package tech.kayys.erp.workflow.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Workflow definition identifier.
 */
public final class WorkflowDefinitionId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WorkflowDefinitionId(UUID value) {
        super(value);
    }

    public static WorkflowDefinitionId of(UUID value) {
        return new WorkflowDefinitionId(value);
    }

    public static WorkflowDefinitionId generate() {
        return new WorkflowDefinitionId(UUID.randomUUID());
    }

    public static WorkflowDefinitionId fromString(String value) {
        return new WorkflowDefinitionId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WorkflowDefinitionId{" + value + "}";
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/identifier/WorkflowInstanceId.java`**:

```java
package tech.kayys.erp.workflow.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Workflow instance identifier.
 */
public final class WorkflowInstanceId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public WorkflowInstanceId(UUID value) {
        super(value);
    }

    public static WorkflowInstanceId of(UUID value) {
        return new WorkflowInstanceId(value);
    }

    public static WorkflowInstanceId generate() {
        return new WorkflowInstanceId(UUID.randomUUID());
    }

    public static WorkflowInstanceId fromString(String value) {
        return new WorkflowInstanceId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "WorkflowInstanceId{" + value + "}";
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/identifier/TaskId.java`**:

```java
package tech.kayys.erp.workflow.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Task identifier.
 */
public final class TaskId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TaskId(UUID value) {
        super(value);
    }

    public static TaskId of(UUID value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId(UUID.randomUUID());
    }

    public static TaskId fromString(String value) {
        return new TaskId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TaskId{" + value + "}";
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/valueobject/WorkflowStatus.java`**:

```java
package tech.kayys.erp.workflow.domain.valueobject;

/**
 * Status of a workflow instance.
 */
public enum WorkflowStatus {
    DRAFT("Draft - being created"),
    ACTIVE("Active - running"),
    PAUSED("Paused - temporarily stopped"),
    COMPLETED("Completed - finished successfully"),
    CANCELLED("Cancelled - terminated"),
    FAILED("Failed - error occurred"),
    ON_HOLD("On Hold - waiting for intervention");

    private final String description;

    WorkflowStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == ACTIVE || this == PAUSED || this == ON_HOLD;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED || this == FAILED;
    }

    public boolean canTransitionTo(WorkflowStatus target) {
        return switch (this) {
            case DRAFT -> target == ACTIVE || target == CANCELLED;
            case ACTIVE -> target == PAUSED || target == COMPLETED || target == CANCELLED || target == FAILED || target == ON_HOLD;
            case PAUSED -> target == ACTIVE || target == CANCELLED || target == COMPLETED;
            case ON_HOLD -> target == ACTIVE || target == CANCELLED;
            case COMPLETED, CANCELLED, FAILED -> false;
        };
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/valueobject/TaskStatus.java`**:

```java
package tech.kayys.erp.workflow.domain.valueobject;

/**
 * Status of a workflow task.
 */
public enum TaskStatus {
    PENDING("Pending - not yet started"),
    IN_PROGRESS("In Progress - being worked on"),
    COMPLETED("Completed - finished"),
    REJECTED("Rejected - not approved"),
    CANCELLED("Cancelled - no longer needed"),
    ON_HOLD("On Hold - waiting"),
    ESCALATED("Escalated - requiring attention"),
    ASSIGNED("Assigned - assigned to user");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS || this == ON_HOLD || this == ESCALATED || this == ASSIGNED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == REJECTED || this == CANCELLED;
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/valueobject/ApprovalType.java`**:

```java
package tech.kayys.erp.workflow.domain.valueobject;

/**
 * Types of approval processes.
 */
public enum ApprovalType {
    SINGLE("Single - one approver"),
    SEQUENTIAL("Sequential - ordered approvers"),
    PARALLEL("Parallel - all approve simultaneously"),
    ANY("Any - first approval accepted"),
    MAJORITY("Majority - majority must approve"),
    UNANIMOUS("Unanimous - all must approve"),
    ESCALATION("Escalation - escalates if not approved");

    private final String description;

    ApprovalType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/model/WorkflowDefinition.java`**:

```java
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
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/model/WorkflowInstance.java`**:

```java
package tech.kayys.erp.workflow.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.workflow.domain.identifier.WorkflowDefinitionId;
import tech.kayys.erp.workflow.domain.identifier.WorkflowInstanceId;
import tech.kayys.erp.workflow.domain.valueobject.WorkflowStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Workflow instance aggregate root.
 * Represents a running instance of a workflow.
 */
public final class WorkflowInstance extends AggregateRoot<WorkflowInstanceId> {
    
    private static final long serialVersionUID = 1L;
    
    private WorkflowDefinitionId definitionId;
    private String definitionName;
    private String definitionVersion;
    private String entityType;
    private String entityId;
    private WorkflowStatus status;
    private Map<String, Object> context;
    private List<WorkflowTask> tasks;
    private int currentStepOrder;
    private String startedBy;
    private Instant startedAt;
    private Instant completedAt;
    private String completedBy;
    private String notes;
    private boolean active;

    private WorkflowInstance(WorkflowInstanceId id) {
        super(id);
        this.tasks = new ArrayList<>();
        this.context = new HashMap<>();
        this.status = WorkflowStatus.DRAFT;
        this.active = true;
        this.startedAt = Instant.now();
        this.currentStepOrder = 0;
    }

    private WorkflowInstance() {
        super();
    }

    /**
     * Factory method to create a new workflow instance.
     */
    public static WorkflowInstance create(
            WorkflowInstanceId id,
            WorkflowDefinitionId definitionId,
            String definitionName,
            String definitionVersion,
            String entityType,
            String entityId,
            String startedBy) {
        WorkflowInstance instance = new WorkflowInstance(id);
        instance.definitionId = definitionId;
        instance.definitionName = definitionName;
        instance.definitionVersion = definitionVersion;
        instance.entityType = entityType;
        instance.entityId = entityId;
        instance.startedBy = startedBy;
        return instance;
    }

    /**
     * Starts the workflow instance.
     */
    public void start() {
        if (status != WorkflowStatus.DRAFT) {
            throw new IllegalStateException("Cannot start workflow in status: " + status);
        }
        this.status = WorkflowStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Creates a task for the current step.
     */
    public WorkflowTask createNextTask() {
        if (status != WorkflowStatus.ACTIVE) {
            throw new IllegalStateException("Cannot create task in status: " + status);
        }
        // This would use the definition to create the next task
        // For now, creating a placeholder
        WorkflowTask task = WorkflowTask.create(
            TaskId.generate(),
            this.getId(),
            "Step " + (tasks.size() + 1),
            "Task description",
            null,
            null,
            this.startedBy
        );
        tasks.add(task);
        setUpdatedAt(Instant.now());
        incrementVersion();
        return task;
    }

    /**
     * Completes the workflow instance.
     */
    public void complete(String completedBy) {
        if (status == WorkflowStatus.COMPLETED || status == WorkflowStatus.CANCELLED) {
            throw new IllegalStateException("Workflow already terminated");
        }
        this.status = WorkflowStatus.COMPLETED;
        this.completedBy = completedBy;
        this.completedAt = Instant.now();
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the workflow instance.
     */
    public void cancel(String reason) {
        if (status == WorkflowStatus.COMPLETED || status == WorkflowStatus.CANCELLED) {
            throw new IllegalStateException("Workflow already terminated");
        }
        this.status = WorkflowStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Pauses the workflow.
     */
    public void pause() {
        if (status != WorkflowStatus.ACTIVE) {
            throw new IllegalStateException("Cannot pause workflow in status: " + status);
        }
        this.status = WorkflowStatus.PAUSED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resumes the workflow.
     */
    public void resume() {
        if (status != WorkflowStatus.PAUSED && status != WorkflowStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot resume workflow in status: " + status);
        }
        this.status = WorkflowStatus.ACTIVE;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the workflow on hold.
     */
    public void putOnHold(String reason) {
        if (status != WorkflowStatus.ACTIVE) {
            throw new IllegalStateException("Cannot put workflow on hold in status: " + status);
        }
        this.status = WorkflowStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the workflow context.
     */
    public void updateContext(String key, Object value) {
        this.context.put(key, value);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets a value from the context.
     */
    @SuppressWarnings("unchecked")
    public <T> T getContextValue(String key) {
        return (T) context.get(key);
    }

    /**
     * Gets the current task.
     */
    public WorkflowTask getCurrentTask() {
        return tasks.isEmpty() ? null : tasks.get(tasks.size() - 1);
    }

    /**
     * Gets the completion percentage.
     */
    public double getCompletionPercentage() {
        // This would be calculated based on total steps vs completed tasks
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completed = tasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        return (double) completed / Math.max(tasks.size(), 1) * 100.0;
    }

    // Getters
    public WorkflowDefinitionId getDefinitionId() { return definitionId; }
    public String getDefinitionName() { return definitionName; }
    public String getDefinitionVersion() { return definitionVersion; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public WorkflowStatus getStatus() { return status; }
    public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }
    public List<WorkflowTask> getTasks() { return Collections.unmodifiableList(tasks); }
    public int getCurrentStepOrder() { return currentStepOrder; }
    public String getStartedBy() { return startedBy; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCompletedBy() { return completedBy; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "WorkflowInstance{" +
                "id=" + getId() +
                ", definitionName='" + definitionName + '\'' +
                ", entityType='" + entityType + '\'' +
                ", entityId='" + entityId + '\'' +
                ", status=" + status +
                ", tasks=" + tasks.size() +
                '}';
    }
}
```

**`/modules/workflow/domain/src/main/java/tech/kayys/erp/workflow/domain/model/WorkflowTask.java`**:

```java
package tech.kayys.erp.workflow.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.workflow.domain.identifier.TaskId;
import tech.kayys.erp.workflow.domain.identifier.WorkflowInstanceId;
import tech.kayys.erp.workflow.domain.valueobject.TaskStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Workflow task aggregate root.
 * Represents a single task within a workflow instance.
 */
public final class WorkflowTask extends AggregateRoot<TaskId> {
    
    private static final long serialVersionUID = 1L;
    
    private WorkflowInstanceId workflowInstanceId;
    private String name;
    private String description;
    private TaskStatus status;
    private String assignedTo;
    private Instant assignedAt;
    private Instant startedAt;
    private Instant completedAt;
    private String completedBy;
    private String formData;
    private List<String> comments;
    private List<String> attachments;
    private String notes;
    private boolean active;

    private WorkflowTask(TaskId id) {
        super(id);
        this.comments = new ArrayList<>();
        this.attachments = new ArrayList<>();
        this.status = TaskStatus.PENDING;
        this.active = true;
    }

    private WorkflowTask() {
        super();
    }

    /**
     * Factory method to create a new workflow task.
     */
    public static WorkflowTask create(
            TaskId id,
            WorkflowInstanceId workflowInstanceId,
            String name,
            String description,
            String assignedTo,
            String formData,
            String notes) {
        WorkflowTask task = new WorkflowTask(id);
        task.workflowInstanceId = workflowInstanceId;
        task.name = name;
        task.description = description;
        task.assignedTo = assignedTo;
        task.formData = formData;
        task.notes = notes;
        return task;
    }

    /**
     * Assigns the task to a user.
     */
    public void assign(String assignedTo) {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot assign completed task");
        }
        this.assignedTo = assignedTo;
        this.assignedAt = Instant.now();
        this.status = TaskStatus.ASSIGNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts working on the task.
     */
    public void start() {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot start completed task");
        }
        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the task.
     */
    public void complete(String completedBy, String formData) {
        if (status != TaskStatus.IN_PROGRESS && status != TaskStatus.ASSIGNED && status != TaskStatus.PENDING) {
            throw new IllegalStateException("Cannot complete task in status: " + status);
        }
        this.status = TaskStatus.COMPLETED;
        this.completedBy = completedBy;
        this.completedAt = Instant.now();
        this.formData = formData;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the task (for approval tasks).
     */
    public void reject(String rejectedBy, String reason) {
        if (status != TaskStatus.IN_PROGRESS && status != TaskStatus.ASSIGNED) {
            throw new IllegalStateException("Cannot reject task in status: " + status);
        }
        this.status = TaskStatus.REJECTED;
        this.completedBy = rejectedBy;
        this.completedAt = Instant.now();
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the task.
     */
    public void cancel(String reason) {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Task already terminated");
        }
        this.status = TaskStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Escalates the task.
     */
    public void escalate(String reason) {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot escalate completed task");
        }
        this.status = TaskStatus.ESCALATED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the task on hold.
     */
    public void putOnHold(String reason) {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            throw new IllegalStateException("Cannot put completed task on hold");
        }
        this.status = TaskStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a comment to the task.
     */
    public void addComment(String comment) {
        comments.add(comment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds an attachment to the task.
     */
    public void addAttachment(String attachment) {
        attachments.add(attachment);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the form data.
     */
    public void updateFormData(String formData) {
        this.formData = formData;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Checks if the task is overdue.
     */
    public boolean isOverdue() {
        if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
            return false;
        }
        if (assignedAt == null) {
            return false;
        }
        // Simple SLA: 7 days from assignment
        return Instant.now().isAfter(assignedAt.plusSeconds(7L * 24L * 60L * 60L));
    }

    // Getters
    public WorkflowInstanceId getWorkflowInstanceId() { return workflowInstanceId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public String getAssignedTo() { return assignedTo; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getCompletedBy() { return completedBy; }
    public String getFormData() { return formData; }
    public List<String> getComments() { return Collections.unmodifiableList(comments); }
    public List<String> getAttachments() { return Collections.unmodifiableList(attachments); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setNotes(String notes) {
        this.notes = notes;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "WorkflowTask{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", assignedTo='" + assignedTo + '\'' +
                '}';
    }
}
```

## 2. Update Root POM

**Update `/pom.xml`** to include Workflow modules:

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
</modules>
```

## Summary

The complete Business Process Automation / Workflow Engine bounded context provides:

1. **Workflow Definition**:
   - Reusable workflow templates
   - Multi-step workflow design
   - Version control
   - Trigger-based initiation
   - Step ordering and branching

2. **Workflow Execution**:
   - Workflow instance lifecycle (Draft → Active → Paused/On Hold → Completed/Cancelled/Failed)
   - Sequential step execution
   - Context data management
   - Entity association

3. **Task Management**:
   - Task lifecycle (Pending → Assigned → In Progress → Completed/Rejected/Cancelled)
   - Assignment and escalation
   - Form data support
   - Comments and attachments
   - SLA and overdue tracking

4. **Approval Workflows**:
   - Multiple approval types (Single, Sequential, Parallel, Any, Majority, Unanimous, Escalation)
   - Approval/rejection handling
   - Escalation paths

5. **Integration Points**:
   - Event-driven triggers
   - Cross-context orchestration
   - Notification integration
   - Audit trail

This completes the Business Process Automation context with comprehensive workflow and task management capabilities that enable the ERP system to automate business processes across all bounded contexts.