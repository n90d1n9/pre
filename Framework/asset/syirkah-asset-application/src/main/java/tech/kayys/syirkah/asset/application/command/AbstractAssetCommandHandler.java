package tech.kayys.syirkah.asset.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.identifier.AssetId;
import tech.kayys.syirkah.asset.domain.model.Asset;
import tech.kayys.syirkah.asset.domain.repository.AssetRepository;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.ApplicationErrorException;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.event.DomainEvent;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.util.List;
import java.util.Objects;

/**
 * Shared orchestration helpers for Asset write use cases.
 *
 * <p>Every write runs inside {@link UnitOfWork} so the aggregate write and the
 * transactional-outbox insert commit atomically (see ASSET-11).</p>
 */
public abstract class AbstractAssetCommandHandler {

    protected final AssetRepository repository;
    protected final EventPublisher eventPublisher;
    protected final UnitOfWork unitOfWork;
    protected final DomainClock clock;

    protected AbstractAssetCommandHandler(
            AssetRepository repository,
            EventPublisher eventPublisher,
            UnitOfWork unitOfWork,
            DomainClock clock
    ) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    protected DomainClock clock() {
        return clock;
    }

    protected Uni<Asset> requireAsset(String tenantId, AssetId assetId) {
        Objects.requireNonNull(tenantId, "tenantId cannot be null");
        return Uni.createFrom()
                .completionStage(() -> repository.findByTenantAndId(tenantId, assetId))
                .map(opt -> opt.orElse(null))
                .onItem().ifNull().failWith(() -> new ApplicationErrorException(
                        ApplicationError.of("asset.not-found", "Asset not found: " + assetId.value())));
    }

    protected Uni<Asset> save(Asset asset) {
        return unitOfWork.execute(() -> Uni.createFrom()
                .completionStage(() -> repository.save(asset))
                .flatMap(saved -> publishPendingEvents(saved).replaceWith(saved)));
    }

    private Uni<Void> publishPendingEvents(Asset asset) {
        List<DomainEvent> events = asset.pullDomainEvents();
        if (events.isEmpty()) {
            return Uni.createFrom().nullItem();
        }
        return eventPublisher.publish(events);
    }
}
