package tech.kayys.erp.hris.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.hris.domain.identifier.TrainingId;
import tech.kayys.erp.hris.domain.valueobject.TrainingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Training program aggregate root.
 * Represents a training program for employees.
 */
public final class TrainingProgram extends AggregateRoot<TrainingId> {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String category;
    private TrainingStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String duration;
    private String location;
    private String instructor;
    private String provider;
    private int maxParticipants;
    private List<String> enrolledEmployees;
    private List<String> completedEmployees;
    private String prerequisites;
    private String learningObjectives;
    private String materials;
    private String evaluation;
    private boolean certified;
    private boolean mandatory;
    private String createdBy;
    private boolean active;

    private TrainingProgram(TrainingId id) {
        super(id);
        this.status = TrainingStatus.PLANNED;
        this.enrolledEmployees = new ArrayList<>();
        this.completedEmployees = new ArrayList<>();
        this.active = true;
        this.maxParticipants = 20;
    }

    private TrainingProgram() {
        super();
    }

    /**
     * Factory method to create a new training program.
     */
    public static TrainingProgram create(
            TrainingId id,
            String name,
            String category,
            LocalDate startDate,
            LocalDate endDate) {
        TrainingProgram program = new TrainingProgram(id);
        program.name = name;
        program.category = category;
        program.startDate = startDate;
        program.endDate = endDate;
        return program;
    }

    /**
     * Enrolls an employee in the training.
     */
    public void enroll(String employeeId) {
        if (status != TrainingStatus.PLANNED) {
            throw new IllegalStateException("Cannot enroll in training in status: " + status);
        }
        if (enrolledEmployees.size() >= maxParticipants) {
            throw new IllegalStateException("Training is full");
        }
        if (enrolledEmployees.contains(employeeId)) {
            throw new IllegalStateException("Employee already enrolled");
        }
        enrolledEmployees.add(employeeId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Removes an employee from training.
     */
    public void withdraw(String employeeId) {
        if (status != TrainingStatus.PLANNED && status != TrainingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot withdraw from training in status: " + status);
        }
        if (!enrolledEmployees.contains(employeeId)) {
            throw new IllegalStateException("Employee not enrolled");
        }
        enrolledEmployees.remove(employeeId);
        completedEmployees.remove(employeeId);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Marks an employee as completed.
     */
    public void completeTraining(String employeeId) {
        if (status != TrainingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete training in status: " + status);
        }
        if (!enrolledEmployees.contains(employeeId)) {
            throw new IllegalStateException("Employee not enrolled");
        }
        if (!completedEmployees.contains(employeeId)) {
            completedEmployees.add(employeeId);
        }
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the training program.
     */
    public void start() {
        if (status != TrainingStatus.PLANNED) {
            throw new IllegalStateException("Cannot start training in status: " + status);
        }
        if (enrolledEmployees.isEmpty()) {
            throw new IllegalStateException("Training has no enrolled employees");
        }
        this.status = TrainingStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the training program.
     */
    public void complete() {
        if (status != TrainingStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete training in status: " + status);
        }
        this.status = TrainingStatus.COMPLETED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the training program.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Training is already terminated");
        }
        this.status = TrainingStatus.CANCELLED;
        this.active = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the completion rate.
     */
    public double getCompletionRate() {
        if (enrolledEmployees.isEmpty()) {
            return 0.0;
        }
        return (double) completedEmployees.size() / enrolledEmployees.size() * 100.0;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public TrainingStatus getStatus() { return status; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getDuration() { return duration; }
    public String getLocation() { return location; }
    public String getInstructor() { return instructor; }
    public String getProvider() { return provider; }
    public int getMaxParticipants() { return maxParticipants; }
    public List<String> getEnrolledEmployees() { return Collections.unmodifiableList(enrolledEmployees); }
    public List<String> getCompletedEmployees() { return Collections.unmodifiableList(completedEmployees); }
    public String getPrerequisites() { return prerequisites; }
    public String getLearningObjectives() { return learningObjectives; }
    public String getMaterials() { return materials; }
    public String getEvaluation() { return evaluation; }
    public boolean isCertified() { return certified; }
    public boolean isMandatory() { return mandatory; }
    public String getCreatedBy() { return createdBy; }
    public boolean isActive() { return active; }

    public void setDescription(String description) {
        this.description = description;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setDuration(String duration) {
        this.duration = duration;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLocation(String location) {
        this.location = location;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setProvider(String provider) {
        this.provider = provider;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setLearningObjectives(String learningObjectives) {
        this.learningObjectives = learningObjectives;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMaterials(String materials) {
        this.materials = materials;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCertified(boolean certified) {
        this.certified = certified;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "TrainingProgram{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", enrolled=" + enrolledEmployees.size() +
                ", completed=" + completedEmployees.size() +
                '}';
    }
}