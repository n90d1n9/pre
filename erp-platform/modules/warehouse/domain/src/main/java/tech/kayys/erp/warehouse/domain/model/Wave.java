package tech.kayys.erp.warehouse.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.warehouse.domain.identifier.WaveId;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;
import tech.kayys.erp.warehouse.domain.valueobject.WaveStatus;
import tech.kayys.erp.warehouse.domain.valueobject.WaveType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Wave aggregate root.
 * Represents a wave of warehouse operations for optimized processing.
 */
public final class Wave extends AggregateRoot<WaveId> {
    
    private static final long serialVersionUID = 1L;
    
    private String waveNumber;
    private WarehouseId warehouseId;
    private WaveType waveType;
    private WaveStatus status;
    private List<WaveTask> tasks;
    private Instant scheduledStartTime;
    private Instant scheduledEndTime;
    private Instant actualStartTime;
    private Instant actualEndTime;
    private int priority;
    private String zone;
    private String assignedTo;
    private int totalTasks;
    private int completedTasks;
    private String notes;
    private boolean active;

    private Wave(WaveId id) {
        super(id);
        this.tasks = new ArrayList<>();
        this.status = WaveStatus.CREATED;
        this.active = true;
        this.priority = 5;
        this.totalTasks = 0;
        this.completedTasks = 0;
    }

    private Wave() {
        super();
    }

    /**
     * Factory method to create a new wave.
     */
    public static Wave create(
            WaveId id,
            String waveNumber,
            WarehouseId warehouseId,
            WaveType waveType,
            Instant scheduledStartTime,
            String createdBy) {
        Wave wave = new Wave(id);
        wave.waveNumber = waveNumber;
        wave.warehouseId = warehouseId;
        wave.waveType = waveType;
        wave.scheduledStartTime = scheduledStartTime;
        wave.createdBy = createdBy;
        return wave;
    }

    /**
     * Adds a task to the wave.
     */
    public void addTask(WaveTask task) {
        if (status != WaveStatus.CREATED && status != WaveStatus.PLANNED) {
            throw new IllegalStateException("Cannot add tasks in status: " + status);
        }
        tasks.add(task);
        totalTasks++;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes a task from the wave.
     */
    public void removeTask(String taskId) {
        if (status != WaveStatus.CREATED && status != WaveStatus.PLANNED) {
            throw new IllegalStateException("Cannot remove tasks in status: " + status);
        }
        tasks.removeIf(t -> t.getTaskId().equals(taskId));
        totalTasks = tasks.size();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Plans the wave.
     */
    public void plan() {
        if (status != WaveStatus.CREATED) {
            throw new IllegalStateException("Cannot plan wave in status: " + status);
        }
        if (tasks.isEmpty()) {
            throw new IllegalStateException("Wave has no tasks");
        }
        this.status = WaveStatus.PLANNED;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the wave.
     */
    public void start() {
        if (status != WaveStatus.PLANNED) {
            throw new IllegalStateException("Cannot start wave in status: " + status);
        }
        this.status = WaveStatus.IN_PROGRESS;
        this.actualStartTime = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes a task in the wave.
     */
    public void completeTask(String taskId, String completedBy) {
        if (status != WaveStatus.IN_PROGRESS && status != WaveStatus.PARTIALLY_COMPLETED) {
            throw new IllegalStateException("Cannot complete task in status: " + status);
        }
        
        WaveTask task = tasks.stream()
            .filter(t -> t.getTaskId().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (task.isCompleted()) {
            throw new IllegalStateException("Task already completed: " + taskId);
        }

        task.complete(completedBy);
        completedTasks++;
        
        // Update status based on progress
        boolean allCompleted = completedTasks >= totalTasks;
        
        if (allCompleted) {
            this.status = WaveStatus.COMPLETED;
            this.actualEndTime = Instant.now();
            this.active = false;
        } else if (completedTasks > 0) {
            this.status = WaveStatus.PARTIALLY_COMPLETED;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Puts the wave on hold.
     */
    public void putOnHold(String reason) {
        if (status == WaveStatus.COMPLETED || status == WaveStatus.CANCELLED) {
            throw new IllegalStateException("Cannot hold wave in status: " + status);
        }
        this.status = WaveStatus.ON_HOLD;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Releases the wave from hold.
     */
    public void release() {
        if (status != WaveStatus.ON_HOLD) {
            throw new IllegalStateException("Cannot release wave in status: " + status);
        }
        this.status = WaveStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the wave.
     */
    public void cancel(String reason) {
        if (status == WaveStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed wave");
        }
        this.status = WaveStatus.CANCELLED;
        this.active = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the completion percentage.
     */
    public double getProgress() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks * 100.0;
    }

    /**
     * Gets the estimated duration in minutes.
     */
    public long getEstimatedDurationMinutes() {
        if (scheduledStartTime == null || scheduledEndTime == null) {
            return 0;
        }
        return java.time.Duration.between(scheduledStartTime, scheduledEndTime).toMinutes();
    }

    /**
     * Gets the actual duration in minutes.
     */
    public long getActualDurationMinutes() {
        if (actualStartTime == null) {
            return 0;
        }
        Instant end = actualEndTime != null ? actualEndTime : Instant.now();
        return java.time.Duration.between(actualStartTime, end).toMinutes();
    }

    /**
     * Gets tasks by status.
     */
    public List<WaveTask> getTasksByStatus(boolean completed) {
        return tasks.stream()
            .filter(t -> t.isCompleted() == completed)
            .collect(java.util.stream.Collectors.toList());
    }

    // Getters
    public String getWaveNumber() { return waveNumber; }
    public WarehouseId getWarehouseId() { return warehouseId; }
    public WaveType getWaveType() { return waveType; }
    public WaveStatus getStatus() { return status; }
    public List<WaveTask> getTasks() { return Collections.unmodifiableList(tasks); }
    public Instant getScheduledStartTime() { return scheduledStartTime; }
    public Instant getScheduledEndTime() { return scheduledEndTime; }
    public Instant getActualStartTime() { return actualStartTime; }
    public Instant getActualEndTime() { return actualEndTime; }
    public int getPriority() { return priority; }
    public String getZone() { return zone; }
    public String getAssignedTo() { return assignedTo; }
    public int getTotalTasks() { return totalTasks; }
    public int getCompletedTasks() { return completedTasks; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setScheduledEndTime(Instant scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPriority(int priority) {
        this.priority = priority;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setZone(String zone) {
        this.zone = zone;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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
        return "Wave{" +
                "id=" + getId() +
                ", waveNumber='" + waveNumber + '\'' +
                ", type=" + waveType +
                ", status=" + status +
                ", tasks=" + tasks.size() +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Wave task value object.
     */
    public static final class WaveTask implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String taskId;
        private final String taskType; // PICK_LIST, PACKING_TASK, SHIPPING_TASK
        private final String taskReference;
        private boolean completed;
        private String completedBy;
        private Instant completedAt;
        private String notes;

        public WaveTask(
                String taskId,
                String taskType,
                String taskReference) {
            this.taskId = taskId;
            this.taskType = taskType;
            this.taskReference = taskReference;
            this.completed = false;
            validate();
        }

        @Override
        public void validate() {
            if (taskId == null || taskId.trim().isEmpty()) {
                throw new IllegalArgumentException("Task ID cannot be empty");
            }
            if (taskType == null || taskType.trim().isEmpty()) {
                throw new IllegalArgumentException("Task type cannot be empty");
            }
        }

        public String getTaskId() { return taskId; }
        public String getTaskType() { return taskType; }
        public String getTaskReference() { return taskReference; }
        public boolean isCompleted() { return completed; }
        public String getCompletedBy() { return completedBy; }
        public Instant getCompletedAt() { return completedAt; }
        public String getNotes() { return notes; }

        public void complete(String completedBy) {
            this.completed = true;
            this.completedBy = completedBy;
            this.completedAt = Instant.now();
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        @Override
        public String toString() {
            return "WaveTask{" +
                    "taskId='" + taskId + '\'' +
                    ", taskType='" + taskType + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }
}