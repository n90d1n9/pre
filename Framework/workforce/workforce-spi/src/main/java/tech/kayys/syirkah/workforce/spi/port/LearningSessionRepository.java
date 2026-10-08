package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.learning.LearningOfferingId;
import tech.kayys.syirkah.workforce.domain.learning.LearningSession;
import tech.kayys.syirkah.workforce.domain.learning.LearningSessionId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface LearningSessionRepository extends Repository<LearningSession, LearningSessionId> {
    CompletionStage<List<LearningSession>> findByOffering(LearningOfferingId offeringId);
}
