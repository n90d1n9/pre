package tech.kayys.erp.project.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.project.domain.identifier.ProjectId;
import tech.kayys.erp.project.domain.valueobject.ProjectStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project aggregate root.
 * Represents a project with tasks, milestones, and resources.
 */
public final class Project extends AggregateRoot<ProjectId> {
    
    private static final long serialVersionUID = 1L;
    
    private String code;
    private String name;
    private String description;
    private ProjectStatus status;
    private String customerId;
    private String customerName;
    private String projectManager;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualEndDate;
    private String priority;
    private String budget;
    private String actualCost;
    private String currencyCode;
    private List<Task> tasks;
    private List<Milestone> milestones;
    private List<ResourceAllocation> resources;
    private List<String> tags;
    private String notes;
    private boolean active;

    private Project(ProjectId id) {
        super(id);
        this.tasks = new ArrayList<>();
        this.milestones = new ArrayList<>();
        this.resources = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.status = ProjectStatus.PLANNING;
        this.active = true;
    }

    private Project() {
        super();
    }

    /**
     * Factory method to create a new project.
     */
    public static Project create(
            ProjectId id,
            String code,
            String name,
            String description,
            String projectManager,
            LocalDate startDate,
            LocalDate endDate,
            String currencyCode) {
        Project project = new Project(id);
        project.code = code;
        project.name = name;
        project.description = description;
        project.projectManager = projectManager;
        project.startDate = startDate;
        project.endDate = endDate;
        project.currencyCode = currencyCode;
        return project;
    }

    /**
     * Approves the project.
     */
    public void approve() {
        if (status != ProjectStatus.PLANNING) {
            throw new IllegalStateException("Cannot approve project in status: " + status);
        }
        this.status = ProjectStatus.APPROVED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the project.
     */
    public void start() {
        if (status != ProjectStatus.APPROVED && status != ProjectStatus.PLANNING) {
            throw new IllegalStateException("Cannot start project in status: " + status);
        }
        this.status = ProjectStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the project on hold.
     */
    public void putOnHold(String reason) {
        if (status != ProjectStatus.IN_PROGRESS && status != ProjectStatus.APPROVED) {
            throw new IllegalStateException("Cannot put project on hold in status: " + status);
        }
        this.status = ProjectStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Resumes the project.
     */
    public void resume() {
        if (status != ProjectStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot resume project in status: " + status);
        }
        this.status = ProjectStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reviews the project.
     */
    public void review() {
        if (status != ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot review project in status: " + status);
        }
        this.status = ProjectStatus.REVIEW;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the project.
     */
    public void complete(LocalDate actualEndDate) {
        if (status != ProjectStatus.REVIEW && status != ProjectStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete project in status: " + status);
        }
        this.status = ProjectStatus.COMPLETED;
        this.actualEndDate = actualEndDate;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the project.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel completed project");
        }
        this.status = ProjectStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a task to the project.
     */
    public void addTask(Task task) {
        tasks.add(task);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a task from the project.
     */
    public void removeTask(String taskId) {
        tasks.removeIf(t -> t.getId().equals(taskId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a milestone to the project.
     */
    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a milestone from the project.
     */
    public void removeMilestone(String milestoneId) {
        milestones.removeIf(m -> m.getId().equals(milestoneId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Allocates a resource to the project.
     */
    public void allocateResource(ResourceAllocation resource) {
        resources.add(resource);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a resource allocation.
     */
    public void removeResource(String resourceId) {
        resources.removeIf(r -> r.getResourceId().equals(resourceId));
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a tag to the project.
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            setUpdatedAt(Instant.now());
            incrementVersion();
        }
    }

    /**
     * Removes a tag from the project.
     */
    public void removeTag(String tag) {
        tags.remove(tag);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the project's completion percentage.
     */
    public double getCompletionPercentage() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completed = tasks.stream()
            .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
            .count();
        return (double) completed / tasks.size() * 100.0;
    }

    /**
     * Gets the number of tasks by status.
     */
    public long getTaskCountByStatus(TaskStatus status) {
        return tasks.stream()
            .filter(t -> t.getStatus() == status)
            .count();
    }

    /**
     * Gets the total estimated hours.
     */
    public double getTotalEstimatedHours() {
        return tasks.stream()
            .mapToDouble(Task::getEstimatedHours)
            .sum();
    }

    /**
     * Gets the total actual hours.
     */
    public double getTotalActualHours() {
        return tasks.stream()
            .mapToDouble(Task::getActualHours)
            .sum();
    }

    // Getters
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ProjectStatus getStatus() { return status; }
    public String getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getProjectManager() { return projectManager; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public LocalDate getActualEndDate() { return actualEndDate; }
    public String getPriority() { return priority; }
    public String getBudget() { return budget; }
    public String getActualCost() { return actualCost; }
    public String getCurrencyCode() { return currencyCode; }
    public List<Task> getTasks() { return Collections.unmodifiableList(tasks); }
    public List<Milestone> getMilestones() { return Collections.unmodifiableList(milestones); }
    public List<ResourceAllocation> getResources() { return Collections.unmodifiableList(resources); }
    public List<String> getTags() { return Collections.unmodifiableList(tags); }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPriority(String priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setBudget(String budget) {
        this.budget = budget;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setActualCost(String actualCost) {
        this.actualCost = actualCost;
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
        return "Project{" +
                "id=" + getId() +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", tasks=" + tasks.size() +
                ", progress=" + getCompletionPercentage() + "%" +
                '}';
    }

    /**
     * Task value object.
     */
    public static final class Task implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String title;
        private final String description;
        private TaskStatus status;
        private TaskPriority priority;
        private String assignedTo;
        private Double estimatedHours;
        private Double actualHours;
        private LocalDate startDate;
        private LocalDate dueDate;
        private LocalDate completedDate;
        private String parentTaskId;
        private List<String> subtasks;
        private String notes;

        public Task(
                String id,
                String title,
                String description,
                TaskPriority priority,
                String assignedTo,
                Double estimatedHours,
                LocalDate dueDate) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.assignedTo = assignedTo;
            this.estimatedHours = estimatedHours;
            this.dueDate = dueDate;
            this.status = TaskStatus.TODO;
            this.subtasks = new ArrayList<>();
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Task ID cannot be empty");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Task title cannot be empty");
            }
            if (priority == null) {
                throw new IllegalArgumentException("Task priority cannot be null");
            }
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public TaskStatus getStatus() { return status; }
        public TaskPriority getPriority() { return priority; }
        public String getAssignedTo() { return assignedTo; }
        public Double getEstimatedHours() { return estimatedHours; }
        public Double getActualHours() { return actualHours; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getDueDate() { return dueDate; }
        public LocalDate getCompletedDate() { return completedDate; }
        public String getParentTaskId() { return parentTaskId; }
        public List<String> getSubtasks() { return Collections.unmodifiableList(subtasks); }
        public String getNotes() { return notes; }

        public void start() {
            if (status != TaskStatus.TODO) {
                throw new IllegalStateException("Cannot start task in status: " + status);
            }
            this.status = TaskStatus.IN_PROGRESS;
            this.startDate = LocalDate.now();
        }

        public void complete() {
            if (status != TaskStatus.REVIEW && status != TaskStatus.IN_PROGRESS) {
                throw new IllegalStateException("Cannot complete task in status: " + status);
            }
            this.status = TaskStatus.COMPLETED;
            this.completedDate = LocalDate.now();
        }

        public void block(String reason) {
            if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
                throw new IllegalStateException("Cannot block completed task");
            }
            this.status = TaskStatus.BLOCKED;
            this.notes = reason;
        }

        public void unblock() {
            if (status != TaskStatus.BLOCKED) {
                throw new IllegalStateException("Task is not blocked");
            }
            this.status = TaskStatus.TODO;
        }

        public void review() {
            if (status != TaskStatus.IN_PROGRESS) {
                throw new IllegalStateException("Cannot review task in status: " + status);
            }
            this.status = TaskStatus.REVIEW;
        }

        public void cancel(String reason) {
            if (status == TaskStatus.COMPLETED) {
                throw new IllegalStateException("Cannot cancel completed task");
            }
            this.status = TaskStatus.CANCELLED;
            this.notes = reason;
        }

        public void addSubtask(String subtaskId) {
            if (!subtasks.contains(subtaskId)) {
                subtasks.add(subtaskId);
            }
        }

        public void removeSubtask(String subtaskId) {
            subtasks.remove(subtaskId);
        }

        public void updateActualHours(double hours) {
            this.actualHours = hours;
        }

        public boolean isOverdue() {
            if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
                return false;
            }
            return dueDate != null && LocalDate.now().isAfter(dueDate);
        }

        @Override
        public String toString() {
            return "Task{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", status=" + status +
                    ", assignedTo='" + assignedTo + '\'' +
                    '}';
        }
    }

    /**
     * Milestone value object.
     */
    public static final class Milestone implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String name;
        private final String description;
        private final LocalDate dueDate;
        private boolean achieved;
        private LocalDate achievedDate;
        private String notes;

        public Milestone(String id, String name, String description, LocalDate dueDate) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.dueDate = dueDate;
            this.achieved = false;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Milestone ID cannot be empty");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Milestone name cannot be empty");
            }
            if (dueDate == null) {
                throw new IllegalArgumentException("Milestone due date cannot be null");
            }
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public LocalDate getDueDate() { return dueDate; }
        public boolean isAchieved() { return achieved; }
        public LocalDate getAchievedDate() { return achievedDate; }
        public String getNotes() { return notes; }

        public void achieve() {
            this.achieved = true;
            this.achievedDate = LocalDate.now();
        }

        public void unachieve() {
            this.achieved = false;
            this.achievedDate = null;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public boolean isOverdue() {
            return !achieved && LocalDate.now().isAfter(dueDate);
        }

        @Override
        public String toString() {
            return "Milestone{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", dueDate=" + dueDate +
                    ", achieved=" + achieved +
                    '}';
        }
    }

    /**
     * Resource allocation value object.
     */
    public static final class ResourceAllocation implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String resourceId;
        private final String resourceName;
        private final String resourceType;
        private final double allocationPercentage;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String role;
        private final String notes;

        public ResourceAllocation(
                String resourceId,
                String resourceName,
                String resourceType,
                double allocationPercentage,
                LocalDate startDate,
                LocalDate endDate,
                String role,
                String notes) {
            this.resourceId = resourceId;
            this.resourceName = resourceName;
            this.resourceType = resourceType;
            this.allocationPercentage = allocationPercentage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.role = role;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (resourceId == null || resourceId.trim().isEmpty()) {
                throw new IllegalArgumentException("Resource ID cannot be empty");
            }
            if (allocationPercentage < 0 || allocationPercentage > 100) {
                throw new IllegalArgumentException("Allocation must be between 0 and 100");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("Start date cannot be null");
            }
            if (endDate != null && endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("End date must be after start date");
            }
        }

        public String getResourceId() { return resourceId; }
        public String getResourceName() { return resourceName; }
        public String getResourceType() { return resourceType; }
        public double getAllocationPercentage() { return allocationPercentage; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getRole() { return role; }
        public String getNotes() { return notes; }

        @Override
        public String toString() {
            return "ResourceAllocation{" +
                    "resourceId='" + resourceId + '\'' +
                    ", resourceName='" + resourceName + '\'' +
                    ", allocation=" + allocationPercentage + "%" +
                    '}';
        }
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
</modules>