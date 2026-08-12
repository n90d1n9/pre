
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Month-end/Year-end closing period.
 */
public final class ClosingPeriod extends AggregateRoot<ClosingPeriodId> {
    
    private static final long serialVersionUID = 1L;
    
    private String fiscalYear;
    private int periodNumber;
    private String periodName;
    private Instant periodStart;
    private Instant periodEnd;
    private ClosingStatus status;
    private List<ClosingTask> tasks;
    private String closedBy;
    private Instant closedAt;
    private String notes;
    private boolean locked;

    private ClosingPeriod(ClosingPeriodId id) {
        super(id);
        this.tasks = new ArrayList<>();
        this.status = ClosingStatus.OPEN;
        this.locked = false;
    }

    private ClosingPeriod() {
        super();
    }

    /**
     * Factory method to create a new closing period.
     */
    public static ClosingPeriod create(
            ClosingPeriodId id,
            String fiscalYear,
            int periodNumber,
            String periodName,
            Instant periodStart,
            Instant periodEnd) {
        ClosingPeriod period = new ClosingPeriod(id);
        period.fiscalYear = fiscalYear;
        period.periodNumber = periodNumber;
        period.periodName = periodName;
        period.periodStart = periodStart;
        period.periodEnd = periodEnd;
        return period;
    }

    /**
     * Adds a closing task.
     */
    public void addTask(ClosingTask task) {
        if (status == ClosingStatus.CLOSED) {
            throw new IllegalStateException("Cannot add tasks to closed period");
        }
        tasks.add(task);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes a task.
     */
    public void completeTask(String taskName, String completedBy) {
        if (status == ClosingStatus.CLOSED) {
            throw new IllegalStateException("Cannot modify closed period");
        }
        
        for (ClosingTask task : tasks) {
            if (task.getName().equals(taskName) && !task.isCompleted()) {
                task.complete(completedBy);
                break;
            }
        }
        
        // Check if all tasks are completed
        boolean allCompleted = tasks.stream().allMatch(ClosingTask::isCompleted);
        if (allCompleted) {
            this.status = ClosingStatus.READY_TO_CLOSE;
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Closes the period.
     */
    public void close(String closedBy) {
        if (status == ClosingStatus.CLOSED) {
            return;
        }
        if (!tasks.stream().allMatch(ClosingTask::isCompleted)) {
            throw new IllegalStateException("All tasks must be completed before closing");
        }
        
        this.status = ClosingStatus.CLOSED;
        this.closedBy = closedBy;
        this.closedAt = Instant.now();
        this.locked = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Reopens a closed period.
     */
    public void reopen(String reason) {
        if (status != ClosingStatus.CLOSED) {
            throw new IllegalStateException("Only closed periods can be reopened");
        }
        
        this.status = ClosingStatus.REOPENED;
        this.locked = false;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the completion percentage.
     */
    public double getCompletionPercentage() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completed = tasks.stream().filter(ClosingTask::isCompleted).count();
        return (double) completed / tasks.size() * 100.0;
    }

    // Getters
    public String getFiscalYear() { return fiscalYear; }
    public int getPeriodNumber() { return periodNumber; }
    public String getPeriodName() { return periodName; }
    public Instant getPeriodStart() { return periodStart; }
    public Instant getPeriodEnd() { return periodEnd; }
    public ClosingStatus getStatus() { return status; }
    public List<ClosingTask> getTasks() { return Collections.unmodifiableList(tasks); }
    public String getClosedBy() { return closedBy; }
    public Instant getClosedAt() { return closedAt; }
    public String getNotes() { return notes; }
    public boolean isLocked() { return locked; }

    @Override
    public String toString() {
        return "ClosingPeriod{" +
                "id=" + getId() +
                ", fiscalYear='" + fiscalYear + '\'' +
                ", periodNumber=" + periodNumber +
                ", status=" + status +
                '}';
    }

    /**
     * Closing status enum.
     */
    public enum ClosingStatus {
        OPEN("Open - in progress"),
        READY_TO_CLOSE("Ready to Close - all tasks complete"),
        CLOSED("Closed - finalized"),
        REOPENED("Reopened - for adjustments");

        private final String description;

        ClosingStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Closing task value object.
     */
    public static final class ClosingTask implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String name;
        private final String description;
        private final String category;
        private boolean completed;
        private String completedBy;
        private Instant completedAt;

        public ClosingTask(String name, String description, String category) {
            this.name = name;
            this.description = description;
            this.category = category;
            this.completed = false;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getCategory() { return category; }
        public boolean isCompleted() { return completed; }
        public String getCompletedBy() { return completedBy; }
        public Instant getCompletedAt() { return completedAt; }

        public void complete(String completedBy) {
            this.completed = true;
            this.completedBy = completedBy;
            this.completedAt = Instant.now();
        }

        @Override
        public String toString() {
            return "ClosingTask{" +
                    "name='" + name + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }
}