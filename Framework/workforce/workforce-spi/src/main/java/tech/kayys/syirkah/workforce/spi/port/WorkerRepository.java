package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.ref.PersonRef;
import tech.kayys.syirkah.workforce.domain.worker.Worker;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Persistence port for the {@link Worker} aggregate root.
 */
public interface WorkerRepository extends Repository<Worker, WorkerId> {

    /**
     * Looks up a worker by their cross-domain person reference.
     */
    CompletionStage<Optional<Worker>> findByPersonRef(PersonRef personRef);
}
