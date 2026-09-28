package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.commercial.ProjectRetention;
import tech.kayys.syirkah.project.domain.commercial.ProjectRetentionId;
import tech.kayys.syirkah.project.domain.commercial.RetentionStatus;
import tech.kayys.syirkah.project.domain.project.ProjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/** Persistence port for retentions. */
public interface ProjectRetentionRepository
        extends Repository<ProjectRetention, ProjectRetentionId> {

    CompletionStage<List<ProjectRetention>> findByProjectId(
            ProjectId projectId
    );

    /** Retentions that still hold a balance (HELD / PARTIALLY_RELEASED). */
    CompletionStage<List<ProjectRetention>> findOpenByProjectId(
            ProjectId projectId
    );
}