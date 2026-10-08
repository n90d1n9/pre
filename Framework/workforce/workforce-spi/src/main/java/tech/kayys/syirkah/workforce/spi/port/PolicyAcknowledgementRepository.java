package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.compliance.ComplianceRequirementId;
import tech.kayys.syirkah.workforce.domain.compliance.PolicyAcknowledgement;
import tech.kayys.syirkah.workforce.domain.compliance.PolicyAcknowledgementId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PolicyAcknowledgementRepository extends Repository<PolicyAcknowledgement, PolicyAcknowledgementId> {
    CompletionStage<Optional<PolicyAcknowledgement>> findByWorkerAndRequirement(WorkerId workerId, ComplianceRequirementId requirementId);
    CompletionStage<List<PolicyAcknowledgement>> findByWorker(WorkerId workerId);
}
