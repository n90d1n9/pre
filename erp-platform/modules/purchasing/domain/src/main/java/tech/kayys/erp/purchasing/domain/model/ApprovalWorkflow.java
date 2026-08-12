package tech.kayys.erp.purchasing.domain.model;

import tech.kayys.erp.foundation.domain.ValueObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Approval workflow value object.
 * Represents the approval process for a purchase order.
 */
public final class ApprovalWorkflow implements ValueObject {
    
    private static final long serialVersionUID = 1L;
    
    private final String workflowId;
    private final String name;
    private final String description;
    private final List<ApprovalStep> steps;
    private final boolean autoApproveOnCompletion;
    private final int maxRejectionCount;

    public ApprovalWorkflow(
            String workflowId,
            String name,
            String description,
            List<ApprovalStep> steps,
            boolean autoApproveOnCompletion,
            int maxRejectionCount) {
        this.workflowId = workflowId;
        this.name = name;
        this.description = description;
        this.steps = steps != null ? new ArrayList<>(steps) : new ArrayList<>();
        this.autoApproveOnCompletion = autoApproveOnCompletion;
        this.maxRejectionCount = maxRejectionCount;
        validate();
    }

    @Override
    public void validate() {
        if (workflowId == null || workflowId.trim().isEmpty()) {
            throw new IllegalArgumentException("Workflow ID cannot be empty");
        }
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("At least one approval step is required");
        }
        if (maxRejectionCount < 0) {
            throw new IllegalArgumentException("Max rejection count cannot be negative");
        }
    }

    public String getWorkflowId() { return workflowId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<ApprovalStep> getSteps() { return Collections.unmodifiableList(steps); }
    public boolean isAutoApproveOnCompletion() { return autoApproveOnCompletion; }
    public int getMaxRejectionCount() { return maxRejectionCount; }

    /**
     * Gets the next step in the workflow.
     */
    public ApprovalStep getNextStep(int currentStepIndex) {
        if (currentStepIndex >= steps.size() - 1) {
            return null;
        }
        return steps.get(currentStepIndex + 1);
    }

    /**
     * Gets the approver for a specific step.
     */
    public String getApproverForStep(int stepIndex) {
        if (stepIndex < 0 || stepIndex >= steps.size()) {
            return null;
        }
        return steps.get(stepIndex).getApproverId();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String workflowId;
        private String name;
        private String description;
        private List<ApprovalStep> steps = new ArrayList<>();
        private boolean autoApproveOnCompletion = true;
        private int maxRejectionCount = 3;

        public Builder workflowId(String workflowId) {
            this.workflowId = workflowId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addStep(ApprovalStep step) {
            this.steps.add(step);
            return this;
        }

        public Builder steps(List<ApprovalStep> steps) {
            this.steps = new ArrayList<>(steps);
            return this;
        }

        public Builder autoApproveOnCompletion(boolean autoApproveOnCompletion) {
            this.autoApproveOnCompletion = autoApproveOnCompletion;
            return this;
        }

        public Builder maxRejectionCount(int maxRejectionCount) {
            this.maxRejectionCount = maxRejectionCount;
            return this;
        }

        public ApprovalWorkflow build() {
            if (workflowId == null) {
                workflowId = java.util.UUID.randomUUID().toString();
            }
            return new ApprovalWorkflow(
                workflowId, name, description, steps,
                autoApproveOnCompletion, maxRejectionCount
            );
        }
    }

    /**
     * Approval step value object.
     */
    public static final class ApprovalStep implements ValueObject {
        private static final long serialVersionUID = 1L;
        
        private final int order;
        private final String approverId;
        private final String approverName;
        private final String role;
        private final double minAmount;
        private final double maxAmount;
        private final boolean canReject;
        private final boolean canDelegate;
        private final String notes;

        public ApprovalStep(
                int order,
                String approverId,
                String approverName,
                String role,
                double minAmount,
                double maxAmount,
                boolean canReject,
                boolean canDelegate,
                String notes) {
            this.order = order;
            this.approverId = approverId;
            this.approverName = approverName;
            this.role = role;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.canReject = canReject;
            this.canDelegate = canDelegate;
            this.notes = notes;
            validate();
        }

        @Override
        public void validate() {
            if (approverId == null || approverId.trim().isEmpty()) {
                throw new IllegalArgumentException("Approver ID cannot be empty");
            }
            if (minAmount < 0) {
                throw new IllegalArgumentException("Min amount cannot be negative");
            }
            if (maxAmount < minAmount) {
                throw new IllegalArgumentException("Max amount must be >= min amount");
            }
        }

        public int getOrder() { return order; }
        public String getApproverId() { return approverId; }
        public String getApproverName() { return approverName; }
        public String getRole() { return role; }
        public double getMinAmount() { return minAmount; }
        public double getMaxAmount() { return maxAmount; }
        public boolean isCanReject() { return canReject; }
        public boolean isCanDelegate() { return canDelegate; }
        public String getNotes() { return notes; }

        public boolean isAmountInRange(double amount) {
            return amount >= minAmount && (maxAmount == 0 || amount <= maxAmount);
        }
    }
}