package tech.kayys.syirkah.asset.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.repository.AssetRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.util.UUID;

public class ActivateAssetHandler extends AbstractAssetCommandHandler
        implements CommandHandler<ActivateAssetCommand, Result<UUID>> {

    public ActivateAssetHandler(AssetRepository repository, EventPublisher eventPublisher,
                                UnitOfWork unitOfWork, DomainClock clock) {
        super(repository, eventPublisher, unitOfWork, clock);
    }

    @Override
    public Uni<Result<UUID>> handle(ActivateAssetCommand command) {
        return requireAsset(command.tenantId(), command.assetId())
                .flatMap(asset -> {
                    asset.activate(clock());
                    return save(asset);
                })
                .map(saved -> Result.success(saved.id().value()));
    }
}
