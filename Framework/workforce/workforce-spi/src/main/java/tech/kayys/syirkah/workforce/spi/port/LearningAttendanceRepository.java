package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningAttendance;
import tech.kayys.syirkah.workforce.domain.learning.LearningAttendanceId;
import tech.kayys.syirkah.workforce.domain.learning.LearningEnrollmentId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface LearningAttendanceRepository extends Repository<LearningAttendance, LearningAttendanceId> {
    CompletionStage<List<LearningAttendance>> findByEnrollment(LearningEnrollmentId enrollmentId);
}
