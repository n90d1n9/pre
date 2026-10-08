package tech.kayys.syirkah.workforce.domain.employment.onboarding.event;

import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.employment.onboarding.OnboardingProcessId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.time.Instant;
import java.util.UUID;

public record OnboardingCompleted(
        UUID eventId,
        Instant occurredAt,
        OnboardingProcessId id,
        WorkerId workerId,
        EmploymentId employmentId
) implements DomainEvent {
    public OnboardingCompleted(OnboardingProcessId id, WorkerId workerId, EmploymentId employmentId) {
        this(UUID.randomUUID(), Instant.now(), id, workerId, employmentId);
    }
    @Override public String eventType() { return "workforce.employment.onboarding.completed"; }
}
