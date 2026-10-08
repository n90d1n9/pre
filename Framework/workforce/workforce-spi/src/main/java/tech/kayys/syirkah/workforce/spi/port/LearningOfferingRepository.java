package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningOffering;
import tech.kayys.syirkah.workforce.domain.learning.LearningOfferingId;
import tech.kayys.syirkah.workforce.domain.learning.LearningProgramId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface LearningOfferingRepository extends Repository<LearningOffering, LearningOfferingId> {
    CompletionStage<List<LearningOffering>> findByProgram(LearningProgramId programId);
}
