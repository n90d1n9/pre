package tech.kayys.syirkah.project.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.project.domain.project.ProjectId;
import tech.kayys.syirkah.project.domain.task.TaskDependency;
import tech.kayys.syirkah.project.domain.task.TaskDependencyId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TaskDependencyRepository
        extends Repository<TaskDependency, TaskDependencyId> {

    CompletionStage<List<TaskDependency>> findByProjectId(ProjectId projectId);
}
