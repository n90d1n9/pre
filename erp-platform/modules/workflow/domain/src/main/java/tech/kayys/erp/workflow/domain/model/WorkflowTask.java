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