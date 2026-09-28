package tech.kayys.syirkah.scheduling.spi;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.scheduling.domain.WorkPattern;
import tech.kayys.syirkah.scheduling.domain.WorkPatternId;

public interface WorkPatternRepository extends Repository<WorkPattern, WorkPatternId> {}
