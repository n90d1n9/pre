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