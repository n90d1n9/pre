package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningCertificate;
import tech.kayys.syirkah.workforce.domain.learning.LearningCertificateId;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface LearningCertificateRepository extends Repository<LearningCertificate, LearningCertificateId> {
    CompletionStage<Optional<LearningCertificate>> findByEnrollment(LearningEnrollmentId enrollmentId);
}
