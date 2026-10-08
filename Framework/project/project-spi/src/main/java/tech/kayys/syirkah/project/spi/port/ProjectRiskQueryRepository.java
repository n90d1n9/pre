package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.project.domain.project.ProjectId;
import tech.kayys.syirkah.project.domain.risk.Issue;
import tech.kayys.syirkah.project.domain.risk.RiskStatus;
import tech.kayys.syirkah.project.domain.risk.RiskCategory;
import tech.kayys.syirkah.project.domain.risk.Probability;
import tech.kayys.syirkah.project.domain.risk.ImpactLevel;
import tech.kayys.syirkah.project.domain.risk.RiskResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * Read side of risk management. Never compute the dashboard by
 * loading every aggregate: these are projections (in-memory in the
 * adapter, SQL read models in production).
 */
public interface ProjectRiskQueryRepository {

    CompletionStage<ProjectRiskSummary> findSummary(ProjectId projectId);

    CompletionStage<List<RiskRegisterRow>> findRiskRegister(ProjectId projectId);

    CompletionStage<List<Issue>> findOpenIssues(ProjectId projectId);
}
