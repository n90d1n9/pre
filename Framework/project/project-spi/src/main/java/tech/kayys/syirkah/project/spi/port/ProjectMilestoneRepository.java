package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.milestone.ProjectMilestone;
import tech.kayys.syirkah.project.domain.milestone.ProjectMilestoneId;
import tech.kayys.syirkah.project.domain.project.ProjectId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Persistence port for project milestones.
 *
 * Like phases, milestones are independent consistency boundaries that
 * reference their project by identifier.
 */
public interface ProjectMilestoneRepository
        extends Repository<ProjectMilestone, ProjectMilestoneId> {

    CompletionStage<List<ProjectMilestone>> findByProjectId(ProjectId projectId);
}
