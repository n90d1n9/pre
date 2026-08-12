package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.AggregateRoot;
import tech.kayys.erp.purchasing.domain.identifier.VendorOnboardingId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Vendor Onboarding aggregate root.
 * Manages the vendor onboarding and qualification process.
 */
public final class VendorOnboarding extends AggregateRoot<VendorOnboardingId> {
    
    private static final long serialVersionUID = 1L;
    
    private String vendorId;
    private String vendorName;
    private String contactEmail;
    private String contactPhone;
    private OnboardingStatus status;
    private List<OnboardingStep> steps;
    private List<OnboardingDocument> documents;
    private String assignedTo;
    private String completedBy;
    private Instant completedAt;
    private String rejectionReason;
    private String notes;
    private boolean active;

    private VendorOnboarding(VendorOnboardingId id) {
        super(id);
        this.steps = new ArrayList<>();
        this.documents = new ArrayList<>();
        this.status = OnboardingStatus.INITIATED;
        this.active = true;
    }

    private VendorOnboarding() {
        super();
    }

    /**
     * Factory method to create a new vendor onboarding.
     */
    public static VendorOnboarding create(
            VendorOnboardingId id,
            String vendorId,
            String vendorName,
            String contactEmail) {
        VendorOnboarding onboarding = new VendorOnboarding(id);
        onboarding.vendorId = vendorId;
        onboarding.vendorName = vendorName;
        onboarding.contactEmail = contactEmail;
        onboarding.addDefaultSteps();
        return onboarding;
    }

    private void addDefaultSteps() {
        steps.add(new OnboardingStep("INFORMATION_GATHERING", "Gather vendor information", false, false));
        steps.add(new OnboardingStep("DOCUMENT_REVIEW", "Review required documents", false, false));
        steps.add(new OnboardingStep("BACKGROUND_CHECK", "Perform background check", false, false));
        steps.add(new OnboardingStep("APPROVAL", "Management approval", false, false));
    }

    /**
     * Starts the onboarding process.
     */
    public void start(String assignedTo) {
        if (status != OnboardingStatus.INITIATED) {
            throw new IllegalStateException("Onboarding already started");
        }
        this.status = OnboardingStatus.IN_PROGRESS;
        this.assignedTo = assignedTo;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Completes a step in the onboarding process.
     */
    public void completeStep(String stepName, String completedBy) {
        for (OnboardingStep step : steps) {
            if (step.getName().equals(stepName)) {
                step.complete(completedBy);
                break;
            }
        }
        
        // Check if all steps are completed
        boolean allCompleted = steps.stream().allMatch(OnboardingStep::isCompleted);
        if (allCompleted) {
            this.status = OnboardingStatus.COMPLETED;
            this.completedBy = completedBy;
            this.completedAt = Instant.now();
        }
        
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Adds a document to the onboarding.
     */
    public void addDocument(OnboardingDocument document) {
        documents.add(document);
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Rejects the onboarding.
     */
    public void reject(String reason) {
        this.status = OnboardingStatus.REJECTED;
        this.rejectionReason = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Suspends the onboarding.
     */
    public void suspend(String reason) {
        this.status = OnboardingStatus.SUSPENDED;
        this.notes = reason;
        setUpdatedAt(Instant.now());
        incrementVersion();
    }

    /**
     * Gets the current step.
     */
    public String getCurrentStep() {
        for (OnboardingStep step : steps) {
            if (!step.isCompleted()) {
                return step.getName();
            }
        }
        return null;
    }

    /**
     * Gets the completion percentage.
     */
    public double getCompletionPercentage() {
        if (steps.isEmpty()) {
            return 0.0;
        }
        long completed = steps.stream().filter(OnboardingStep::isCompleted).count();
        return (double) completed / steps.size() * 100.0;
    }

    // Getters
    public String getVendorId() { return vendorId; }
    public String getVendorName() { return vendorName; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public OnboardingStatus getStatus() { return status; }
    public List<OnboardingStep> getSteps() { return Collections.unmodifiableList(steps); }
    public List<OnboardingDocument> getDocuments() { return Collections.unmodifiableList(documents); }
    public String getAssignedTo() { return assignedTo; }
    public String getCompletedBy() { return completedBy; }
    public Instant getCompletedAt() { return completedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public String getNotes() { return notes; }
    public boolean isActive() { return active; }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
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
        return "VendorOnboarding{" +
                "id=" + getId() +
                ", vendorName='" + vendorName + '\'' +
                ", status=" + status +
                ", completion=" + getCompletionPercentage() + "%" +
                '}';
    }

    /**
     * Onboarding status enum.
     */
    public enum OnboardingStatus {
        INITIATED("Initiated"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        REJECTED("Rejected"),
        SUSPENDED("Suspended");

        private final String description;

        OnboardingStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Onboarding step value object.
     */
    public static final class OnboardingStep {
        private final String name;
        private final String description;
        private boolean completed;
        private String completedBy;
        private Instant completedAt;

        public OnboardingStep(String name, String description, boolean completed, boolean required) {
            this.name = name;
            this.description = description;
            this.completed = completed;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public boolean isCompleted() { return completed; }
        public String getCompletedBy() { return completedBy; }
        public Instant getCompletedAt() { return completedAt; }

        public void complete(String completedBy) {
            this.completed = true;
            this.completedBy = completedBy;
            this.completedAt = Instant.now();
        }
    }

    /**
     * Onboarding document value object.
     */
    public static final class OnboardingDocument {
        private final String documentId;
        private final String documentType;
        private final String documentName;
        private final String fileUrl;
        private final Instant uploadedAt;
        private final String uploadedBy;
        private final boolean verified;
        private final String verifiedBy;
        private final Instant verifiedAt;

        public OnboardingDocument(
                String documentId,
                String documentType,
                String documentName,
                String fileUrl,
                Instant uploadedAt,
                String uploadedBy,
                boolean verified,
                String verifiedBy,
                Instant verifiedAt) {
            this.documentId = documentId;
            this.documentType = documentType;
            this.documentName = documentName;
            this.fileUrl = fileUrl;
            this.uploadedAt = uploadedAt;
            this.uploadedBy = uploadedBy;
            this.verified = verified;
            this.verifiedBy = verifiedBy;
            this.verifiedAt = verifiedAt;
        }

        public String getDocumentId() { return documentId; }
        public String getDocumentType() { return documentType; }
        public String getDocumentName() { return documentName; }
        public String getFileUrl() { return fileUrl; }
        public Instant getUploadedAt() { return uploadedAt; }
        public String getUploadedBy() { return uploadedBy; }
        public boolean isVerified() { return verified; }
        public String getVerifiedBy() { return verifiedBy; }
        public Instant getVerifiedAt() { return verifiedAt; }
    }
}