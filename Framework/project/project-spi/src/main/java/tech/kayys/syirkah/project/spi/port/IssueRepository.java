package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.project.ProjectId;
import tech.kayys.syirkah.project.domain.risk.Issue;
import tech.kayys.syirkah.project.domain.risk.IssueId;
import tech.kayys.syirkah.project.domain.risk.RiskId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface IssueRepository
        extends Repository<Issue, IssueId> {

    CompletionStage<Optional<Issue>> findByNumber(ProjectId projectId, String number);

    CompletionStage<List<Issue>> findOpenByProjectId(ProjectId projectId);

    CompletionStage<List<Issue>> findByRiskId(RiskId riskId);
}
