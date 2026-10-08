package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.socialinsurance.WorkerSocialInsuranceEnrollment;
import tech.kayys.syirkah.workforce.domain.socialinsurance.WorkerSocialInsuranceEnrollmentId;
import tech.kayys.syirkah.workforce.spi.port.EmploymentRepository;
import tech.kayys.syirkah.workforce.spi.port.SocialInsuranceSchemeRepository;
import tech.kayys.syirkah.workforce.spi.port.WorkerRepository;
import tech.kayys.syirkah.workforce.spi.port.WorkerSocialInsuranceEnrollmentRepository;

import java.util.Objects;

public class EnrollWorkerSocialInsuranceHandler implements CommandHandler<EnrollWorkerSocialInsuranceCommand, Result<WorkerSocialInsuranceEnrollmentId>> {

    private final WorkerRepository workerRepository;
    private final EmploymentRepository employmentRepository;
    private final SocialInsuranceSchemeRepository schemeRepository;
    private final WorkerSocialInsuranceEnrollmentRepository enrollmentRepository;
    private final EventPublisher eventPublisher;

    public EnrollWorkerSocialInsuranceHandler(
            WorkerRepository workerRepository,
            EmploymentRepository employmentRepository,
            SocialInsuranceSchemeRepository schemeRepository,
            WorkerSocialInsuranceEnrollmentRepository enrollmentRepository,
            EventPublisher eventPublisher
    ) {
        this.workerRepository = Objects.requireNonNull(workerRepository);
        this.employmentRepository = Objects.requireNonNull(employmentRepository);
        this.schemeRepository = Objects.requireNonNull(schemeRepository);
        this.enrollmentRepository = Objects.requireNonNull(enrollmentRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<WorkerSocialInsuranceEnrollmentId>> handle(EnrollWorkerSocialInsuranceCommand cmd) {
        return Uni.createFrom().completionStage(workerRepository.findById(cmd.workerId()))
                .chain(optWorker -> {
                    if (optWorker.isEmpty()) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("WORKER_NOT_FOUND", "Worker not found")));
                    }
                    return Uni.createFrom().completionStage(employmentRepository.findById(cmd.employmentId()))
                            .chain(optEmployment -> {
                                if (optEmployment.isEmpty()) {
                                    return Uni.createFrom().item(Result.failure(ApplicationError.of("EMPLOYMENT_NOT_FOUND", "Employment not found")));
                                }
                                return Uni.createFrom().completionStage(schemeRepository.findById(cmd.schemeId()))
                                        .chain(optScheme -> {
                                            if (optScheme.isEmpty()) {
                                                return Uni.createFrom().item(Result.failure(ApplicationError.of("SCHEME_NOT_FOUND", "Scheme not found")));
                                            }
                                            if (!optScheme.get().isActive()) {
                                                return Uni.createFrom().item(Result.failure(ApplicationError.of("SCHEME_NOT_ACTIVE", "Scheme is not active")));
                                            }
                                            WorkerSocialInsuranceEnrollment enrollment;
                                            try {
                                                enrollment = WorkerSocialInsuranceEnrollment.create(
                                                        WorkerSocialInsuranceEnrollmentId.generate(),
                                                        cmd.workerId(),
                                                        cmd.employmentId(),
                                                        cmd.schemeId(),
                                                        cmd.membershipNumber(),
                                                        cmd.effectiveFrom()
                                                );
                                            } catch (Exception e) {
                                                return Uni.createFrom().item(Result.failure(ApplicationError.of("INVALID_ENROLLMENT", e.getMessage())));
                                            }
                                            return Uni.createFrom().completionStage(enrollmentRepository.save(enrollment))
                                                    .chain(saved -> eventPublisher.publish(saved.pullDomainEvents())
                                                            .replaceWith(Result.success(saved.getId())));
                                        });
                            });
                });
    }
}
