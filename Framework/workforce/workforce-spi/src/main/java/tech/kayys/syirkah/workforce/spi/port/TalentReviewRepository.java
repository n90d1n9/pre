package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.talent.TalentReview;
import tech.kayys.syirkah.workforce.domain.talent.TalentReviewId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface TalentReviewRepository extends Repository<TalentReview, TalentReviewId> {
    CompletionStage<List<TalentReview>> findByWorker(WorkerId workerId);
}
