package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.commercial.ProjectAdvance;
import tech.kayys.syirkah.project.domain.commercial.ProjectAdvanceId;
import tech.kayys.syirkah.project.domain.project.ProjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/** Persistence port for advances. */
public interface ProjectAdvanceRepository
        extends Repository<ProjectAdvance, ProjectAdvanceId> {

    CompletionStage<List<ProjectAdvance>> findByProjectId(
            ProjectId projectId
    );
}