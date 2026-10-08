package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceAssessment;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceAssessmentId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface ComplianceAssessmentRepository extends Repository<ComplianceAssessment, ComplianceAssessmentId> {
    CompletionStage<Optional<ComplianceAssessment>> findLatestByWorkerAndRequirement(WorkerId workerId, ComplianceRequirementId requirementId);
    CompletionStage<List<ComplianceAssessment>> findByWorker(WorkerId workerId);
}
