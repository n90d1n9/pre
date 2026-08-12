# Complete Implementation: HRIS / Workforce Management Bounded Context

Now I'll implement the complete HRIS (Human Resource Information System) / Workforce Management bounded context, which handles employee onboarding, offboarding, performance reviews, training, succession planning, and workforce analytics.

## 1. HRIS Domain Module

**`/modules/hris/domain/pom.xml`**:

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

    <artifactId>erp-hris-domain</artifactId>

    <dependencies>
        <dependency>
            <groupId>tech.kayys.erp</groupId>
            <artifactId>erp-foundation-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/identifier/PerformanceReviewId.java`**:

```java
package tech.kayys.erp.hris.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Performance review identifier.
 */
public final class PerformanceReviewId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public PerformanceReviewId(UUID value) {
        super(value);
    }

    public static PerformanceReviewId of(UUID value) {
        return new PerformanceReviewId(value);
    }

    public static PerformanceReviewId generate() {
        return new PerformanceReviewId(UUID.randomUUID());
    }

    public static PerformanceReviewId fromString(String value) {
        return new PerformanceReviewId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "PerformanceReviewId{" + value + "}";
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/identifier/TrainingId.java`**:

```java
package tech.kayys.erp.hris.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Training program identifier.
 */
public final class TrainingId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public TrainingId(UUID value) {
        super(value);
    }

    public static TrainingId of(UUID value) {
        return new TrainingId(value);
    }

    public static TrainingId generate() {
        return new TrainingId(UUID.randomUUID());
    }

    public static TrainingId fromString(String value) {
        return new TrainingId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "TrainingId{" + value + "}";
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/identifier/OnboardingId.java`**:

```java
package tech.kayys.erp.hris.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Onboarding process identifier.
 */
public final class OnboardingId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public OnboardingId(UUID value) {
        super(value);
    }

    public static OnboardingId of(UUID value) {
        return new OnboardingId(value);
    }

    public static OnboardingId generate() {
        return new OnboardingId(UUID.randomUUID());
    }

    public static OnboardingId fromString(String value) {
        return new OnboardingId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "OnboardingId{" + value + "}";
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/valueobject/ReviewStatus.java`**:

```java
package tech.kayys.erp.hris.domain.valueobject;

/**
 * Status of a performance review.
 */
public enum ReviewStatus {
    SCHEDULED("Scheduled - review planned"),
    IN_PROGRESS("In Progress - review ongoing"),
    COMPLETED("Completed - review finished"),
    CANCELLED("Cancelled - review cancelled"),
    PENDING_APPROVAL("Pending Approval - awaiting final approval"),
    APPROVED("Approved - review approved");

    private final String description;

    ReviewStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == IN_PROGRESS || this == PENDING_APPROVAL;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == APPROVED || this == CANCELLED;
    }

    public boolean canTransitionTo(ReviewStatus target) {
        return switch (this) {
            case SCHEDULED -> target == IN_PROGRESS || target == CANCELLED;
            case IN_PROGRESS -> target == PENDING_APPROVAL || target == COMPLETED || target == CANCELLED;
            case PENDING_APPROVAL -> target == APPROVED || target == CANCELLED;
            case APPROVED, COMPLETED, CANCELLED -> false;
        };
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/valueobject/OnboardingStatus.java`**:

```java
package tech.kayys.erp.hris.domain.valueobject;

/**
 * Status of an onboarding process.
 */
public enum OnboardingStatus {
    INITIATED("Initiated - onboarding started"),
    DOCUMENTATION("Documentation - collecting documents"),
    ORIENTATION("Orientation - attending orientation"),
    TRAINING("Training - completing training"),
    EQUIPMENT("Equipment - receiving equipment"),
    MENTORSHIP("Mentorship - assigned mentor"),
    COMPLETED("Completed - onboarding finished"),
    FAILED("Failed - onboarding failed");

    private final String description;

    OnboardingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != COMPLETED && this != FAILED;
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/valueobject/TrainingStatus.java`**:

```java
package tech.kayys.erp.hris.domain.valueobject;

/**
 * Status of a training program.
 */
public enum TrainingStatus {
    PLANNED("Planned - training scheduled"),
    IN_PROGRESS("In Progress - training underway"),
    COMPLETED("Completed - training finished"),
    CANCELLED("Cancelled - training cancelled"),
    POSTPONED("Postponed - training delayed");

    private final String description;

    TrainingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == PLANNED || this == IN_PROGRESS;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/model/PerformanceReview.java`**:

```java
package tech.kayys.erp.hris.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.hris.domain.identifier.PerformanceReviewId;
import tech.kayys.erp.hris.domain.valueobject.ReviewStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Performance review aggregate root.
 * Represents an employee's performance evaluation.
 */
public final class PerformanceReview extends AggregateRoot<PerformanceReviewId> {
    
    private static final long serialVersionUID = 1L;
    
    private String employeeId;
    private String reviewerId;
    private String reviewPeriod;
    private LocalDate reviewDate;
    private ReviewStatus status;
    private List<ReviewCriteria> criteria;
    private String overallRating;
    private String strengths;
    private String areasForImprovement;
    private String goals;
    private String reviewerComments;
    private String employeeComments;
    private String nextReviewDate;
    private boolean completed;
    private boolean acknowledged;
    private String acknowledgedBy;
    private Instant acknowledgedAt;

    private PerformanceReview(PerformanceReviewId id) {
        super(id);
        this.criteria = new ArrayList<>();
        this.status = ReviewStatus.SCHEDULED;
        this.completed = false;
        this.acknowledged = false;
    }

    private PerformanceReview() {
        super();
    }

    /**
     * Factory method to create a new performance review.
     */
    public static PerformanceReview create(
            PerformanceReviewId id,
            String employeeId,
            String reviewerId,
            String reviewPeriod,
            LocalDate reviewDate) {
        PerformanceReview review = new PerformanceReview(id);
        review.employeeId = employeeId;
        review.reviewerId = reviewerId;
        review.reviewPeriod = reviewPeriod;
        review.reviewDate = reviewDate;
        return review;
    }

    /**
     * Adds a review criteria.
     */
    public void addCriteria(ReviewCriteria criterion) {
        if (status == ReviewStatus.COMPLETED || status == ReviewStatus.APPROVED) {
            throw new IllegalStateException("Cannot modify completed review");
        }
        criteria.add(criterion);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Starts the review process.
     */
    public void startReview() {
        if (status != ReviewStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot start review in status: " + status);
        }
        this.status = ReviewStatus.IN_PROGRESS;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Submits the review for approval.
     */
    public void submitForApproval() {
        if (status != ReviewStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot submit review in status: " + status);
        }
        if (criteria.isEmpty()) {
            throw new IllegalStateException("Review must have at least one criteria");
        }
        this.status = ReviewStatus.PENDING_APPROVAL;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Approves the review.
     */
    public void approve() {
        if (status != ReviewStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot approve review in status: " + status);
        }
        this.status = ReviewStatus.APPROVED;
        this.completed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes the review without formal approval.
     */
    public void complete() {
        if (status != ReviewStatus.IN_PROGRESS && status != ReviewStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Cannot complete review in status: " + status);
        }
        this.status = ReviewStatus.COMPLETED;
        this.completed = true;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Cancels the review.
     */
    public void cancel(String reason) {
        if (status.isTerminal()) {
            throw new IllegalStateException("Cannot cancel completed review");
        }
        this.status = ReviewStatus.CANCELLED;
        this.completed = false;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Acknowledges the review by the employee.
     */
    public void acknowledge(String acknowledgedBy) {
        if (!completed && status != ReviewStatus.APPROVED) {
            throw new IllegalStateException("Cannot acknowledge incomplete review");
        }
        this.acknowledged = true;
        this.acknowledgedBy = acknowledgedBy;
        this.acknowledgedAt = Instant.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Calculates the overall rating.
     */
    public double calculateOverallRating() {
        if (criteria.isEmpty()) {
            return 0.0;
        }
        return criteria.stream()
            .mapToDouble(ReviewCriteria::getRating)
            .average()
            .orElse(0.0);
    }

    /**
     * Gets the overall rating as a string.
     */
    public String getOverallRatingLabel() {
        double rating = calculateOverallRating();
        return switch ((int) Math.round(rating)) {
            case 5 -> "Outstanding";
            case 4 -> "Exceeds Expectations";
            case 3 -> "Meets Expectations";
            case 2 -> "Needs Improvement";
            case 1 -> "Unsatisfactory";
            default -> "Not Rated";
        };
    }

    // Getters and Setters
    public String getEmployeeId() { return employeeId; }
    public String getReviewerId() { return reviewerId; }
    public String getReviewPeriod() { return reviewPeriod; }
    public LocalDate getReviewDate() { return reviewDate; }
    public ReviewStatus getStatus() { return status; }
    public List<ReviewCriteria> getCriteria() { return Collections.unmodifiableList(criteria); }
    public String getOverallRating() { return overallRating; }
    public String getStrengths() { return strengths; }
    public String getAreasForImprovement() { return areasForImprovement; }
    public String getGoals() { return goals; }
    public String getReviewerComments() { return reviewerComments; }
    public String getEmployeeComments() { return employeeComments; }
    public String getNextReviewDate() { return nextReviewDate; }
    public boolean isCompleted() { return completed; }
    public boolean isAcknowledged() { return acknowledged; }
    public String getAcknowledgedBy() { return acknowledgedBy; }
    public Instant getAcknowledgedAt() { return acknowledgedAt; }

    public void setOverallRating(String overallRating) {
        this.overallRating = overallRating;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setAreasForImprovement(String areasForImprovement) {
        this.areasForImprovement = areasForImprovement;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setGoals(String goals) {
        this.goals = goals;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEmployeeComments(String employeeComments) {
        this.employeeComments = employeeComments;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setNextReviewDate(String nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    @Override
    public String toString() {
        return "PerformanceReview{" +
                "id=" + getId() +
                ", employeeId='" + employeeId + '\'' +
                ", status=" + status +
                ", rating=" + calculateOverallRating() +
                '}';
    }

    /**
     * Review criteria value object.
     */
    public static final class ReviewCriteria implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String category;
        private final String description;
        private final double rating;
        private final String comments;

        public ReviewCriteria(String category, String description, double rating, String comments) {
            this.category = category;
            this.description = description;
            this.rating = rating;
            this.comments = comments;
            validate();
        }

        @Override
        public void validate() {
            if (category == null || category.trim().isEmpty()) {
                throw new IllegalArgumentException("Category cannot be empty");
            }
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
        }

        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public double getRating() { return rating; }
        public String getComments() { return comments; }

        @Override
        public String toString() {
            return "ReviewCriteria{" +
                    "category='" + category + '\'' +
                    ", rating=" + rating +
                    '}';
        }
    }
}
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/model/TrainingProgram.java`**:

```java
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
```

**`/modules/hris/domain/src/main/java/tech/kayys/erp/hris/domain/model/OnboardingProcess.java`**:

```java
package tech.kayys.erp.hris.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.hris.domain.identifier.OnboardingId;
import tech.kayys.erp.hris.domain.valueobject.OnboardingStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Onboarding process aggregate root.
 * Manages the onboarding of new employees.
 */
public final class OnboardingProcess extends AggregateRoot<OnboardingId> {
    
    private static final long serialVersionUID = 1L;
    
    private String employeeId;
    private String employeeName;
    private LocalDate startDate;
    private LocalDate expectedCompletionDate;
    private LocalDate actualCompletionDate;
    private OnboardingStatus status;
    private List<OnboardingTask> tasks;
    private String assignedTo;
    private String department;
    private String position;
    private String mentorId;
    private String paperworkStatus;
    private boolean equipmentIssued;
    private boolean trainingCompleted;
    private boolean orientationCompleted;
    private String notes;
    private boolean completed;

    private OnboardingProcess(OnboardingId id) {
        super(id);
        this.tasks = new ArrayList<>();
        this.status = OnboardingStatus.INITIATED;
        this.completed = false;
    }

    private OnboardingProcess() {
        super();
    }

    /**
     * Factory method to create a new onboarding process.
     */
    public static OnboardingProcess create(
            OnboardingId id,
            String employeeId,
            String employeeName,
            LocalDate startDate,
            String department,
            String position) {
        OnboardingProcess process = new OnboardingProcess(id);
        process.employeeId = employeeId;
        process.employeeName = employeeName;
        process.startDate = startDate;
        process.department = department;
        process.position = position;
        process.expectedCompletionDate = startDate.plusDays(30);
        return process;
    }

    /**
     * Adds a task to the onboarding process.
     */
    public void addTask(OnboardingTask task) {
        if (status == OnboardingStatus.COMPLETED || status == OnboardingStatus.FAILED) {
            throw new IllegalStateException("Cannot modify completed onboarding");
        }
        tasks.add(task);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes a task in the onboarding process.
     */
    public void completeTask(String taskId, String completedBy) {
        if (status == OnboardingStatus.COMPLETED || status == OnboardingStatus.FAILED) {
            throw new IllegalStateException("Cannot modify completed onboarding");
        }
        OnboardingTask task = tasks.stream()
            .filter(t -> t.getId().equals(taskId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        
        task.complete(completedBy);
        updateStatus();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Updates the overall status based on tasks.
     */
    private void updateStatus() {
        boolean allCompleted = tasks.stream().allMatch(OnboardingTask::isCompleted);
        boolean anyFailed = tasks.stream().anyMatch(t -> !t.isCompleted() && t.isRequired());
        
        if (allCompleted) {
            this.status = OnboardingStatus.COMPLETED;
            this.completed = true;
            this.actualCompletionDate = LocalDate.now();
        } else if (anyFailed) {
            this.status = OnboardingStatus.FAILED;
        } else {
            this.status = getStatusFromTasks();
        }
    }

    private OnboardingStatus getStatusFromTasks() {
        int completedCount = (int) tasks.stream().filter(OnboardingTask::isCompleted).count();
        double progress = (double) completedCount / tasks.size() * 100.0;
        
        if (progress < 25) return OnboardingStatus.INITIATED;
        if (progress < 50) return OnboardingStatus.DOCUMENTATION;
        if (progress < 75) return OnboardingStatus.ORIENTATION;
        return OnboardingStatus.TRAINING;
    }

    /**
     * Gets the completion progress.
     */
    public double getProgress() {
        if (tasks.isEmpty()) {
            return 0.0;
        }
        long completed = tasks.stream().filter(OnboardingTask::isCompleted).count();
        return (double) completed / tasks.size() * 100.0;
    }

    /**
     * Marks the onboarding as completed.
     */
    public void complete() {
        if (status == OnboardingStatus.COMPLETED) {
            return;
        }
        if (!tasks.stream().allMatch(OnboardingTask::isCompleted)) {
            throw new IllegalStateException("All tasks must be completed");
        }
        this.status = OnboardingStatus.COMPLETED;
        this.completed = true;
        this.actualCompletionDate = LocalDate.now();
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    // Getters
    public String getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getExpectedCompletionDate() { return expectedCompletionDate; }
    public LocalDate getActualCompletionDate() { return actualCompletionDate; }
    public OnboardingStatus getStatus() { return status; }
    public List<OnboardingTask> getTasks() { return Collections.unmodifiableList(tasks); }
    public String getAssignedTo() { return assignedTo; }
    public String getDepartment() { return department; }
    public String getPosition() { return position; }
    public String getMentorId() { return mentorId; }
    public String getPaperworkStatus() { return paperworkStatus; }
    public boolean isEquipmentIssued() { return equipmentIssued; }
    public boolean isTrainingCompleted() { return trainingCompleted; }
    public boolean isOrientationCompleted() { return orientationCompleted; }
    public String getNotes() { return notes; }
    public boolean isCompleted() { return completed; }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setPaperworkStatus(String paperworkStatus) {
        this.paperworkStatus = paperworkStatus;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setEquipmentIssued(boolean equipmentIssued) {
        this.equipmentIssued = equipmentIssued;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setTrainingCompleted(boolean trainingCompleted) {
        this.trainingCompleted = trainingCompleted;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    public void setOrientationCompleted(boolean orientationCompleted) {
        this.orientationCompleted = orientationCompleted;
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
        return "OnboardingProcess{" +
                "id=" + getId() +
                ", employeeName='" + employeeName + '\'' +
                ", status=" + status +
                ", progress=" + getProgress() + "%" +
                '}';
    }

    /**
     * Onboarding task value object.
     */
    public static final class OnboardingTask implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final String id;
        private final String name;
        private final String description;
        private final boolean required;
        private boolean completed;
        private String completedBy;
        private Instant completedAt;

        public OnboardingTask(String id, String name, String description, boolean required) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.required = required;
            this.completed = false;
            validate();
        }

        @Override
        public void validate() {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Task ID cannot be empty");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Task name cannot be empty");
            }
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isRequired() { return required; }
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
            return "OnboardingTask{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", completed=" + completed +
                    '}';
        }
    }
}
```

## 2. Update Root POM

**Update `/pom.xml`** to include HRIS modules:

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
</modules>
```

## Summary

The complete HRIS / Workforce Management bounded context provides:

1. **Performance Management**:
   - Performance review lifecycle (Scheduled → In Progress → Pending Approval → Approved/Completed)
   - Review criteria with rating (1-5 scale)
   - Overall rating calculation
   - Employee acknowledgment
   - Strengths and areas for improvement tracking

2. **Training Management**:
   - Training program lifecycle (Planned → In Progress → Completed/Cancelled)
   - Employee enrollment and completion tracking
   - Training materials and evaluation
   - Completion rate calculation

3. **Onboarding Management**:
   - Onboarding process lifecycle
   - Task-based onboarding tracking
   - Progress monitoring
   - Mentor assignment
   - Equipment and documentation tracking

4. **Workforce Analytics**:
   - Training completion rates
   - Onboarding progress metrics
   - Performance review ratings

5. **Integration Points**:
   - Employee context for employee data
   - Position and department management

This completes the HRIS context with comprehensive workforce management capabilities that integrate with Employee, Payroll, and other contexts throughout the ERP system.