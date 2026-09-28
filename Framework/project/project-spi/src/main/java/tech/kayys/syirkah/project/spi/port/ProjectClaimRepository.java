package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.commercial.ProjectClaim;
import tech.kayys.syirkah.project.domain.commercial.ProjectClaimId;
import tech.kayys.syirkah.project.domain.project.ProjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/** Persistence port for project claims. */
public interface ProjectClaimRepository
        extends Repository<ProjectClaim, ProjectClaimId> {

    CompletionStage<List<ProjectClaim>> findByProjectId(
            ProjectId projectId
    );
}