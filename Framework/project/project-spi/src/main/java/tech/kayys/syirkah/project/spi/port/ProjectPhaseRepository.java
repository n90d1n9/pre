package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.phase.ProjectPhase;
import tech.kayys.syirkah.project.domain.phase.ProjectPhaseId;
import tech.kayys.syirkah.project.domain.project.ProjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Persistence port for project phases.
 *
 * Phases are their own aggregate roots, linked to the project by
 * identifier - which is why they get their own repository instead of
 * being loaded through the Project aggregate.
 */
public interface ProjectPhaseRepository
        extends Repository<ProjectPhase, ProjectPhaseId> {

    CompletionStage<List<ProjectPhase>> findByProjectId(ProjectId projectId);
}
