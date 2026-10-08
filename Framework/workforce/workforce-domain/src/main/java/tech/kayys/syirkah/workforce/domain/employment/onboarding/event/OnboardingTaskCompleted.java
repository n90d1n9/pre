package tech.kayys.syirkah.workforce.domain.employment.onboarding.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.onboarding.OnboardingProcessId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record OnboardingTaskCompleted(
        UUID eventId,
        Instant occurredAt,
        OnboardingProcessId id,
        WorkerId workerId,
        String taskId,
        String completedBy
) implements DomainEvent {
    public OnboardingTaskCompleted(OnboardingProcessId id, WorkerId workerId, String taskId, String completedBy) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, taskId, completedBy);
    }
    @Override public String eventType() { return "workforce.employment.onboarding.task_completed"; }
}
