package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceEvidence;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceEvidenceId;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface ComplianceEvidenceRepository extends Repository<ComplianceEvidence, ComplianceEvidenceId> {
    CompletionStage<List<ComplianceEvidence>> findByWorkerAndRequirement(WorkerId workerId, ComplianceRequirementId requirementId);
    CompletionStage<List<ComplianceEvidence>> findByWorker(WorkerId workerId);
}
