package tech.kayys.syirkah.asset.application.inspection;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.inspection.AssetInspectionId;
import tech.kayys.syirkah.asset.domain.repository.AssetInspectionRepository;
import tech.kayys.syirkah.asset.domain.repository.AssetRepository;
import tech.kayys.syirkah.asset.domain.repository.InspectionFindingRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

public class StartAssetInspectionHandler extends AbstractAssetInspectionHandler
        implements CommandHandler<StartAssetInspectionCommand, Result<AssetInspectionReadModel>> {

    public StartAssetInspectionHandler(AssetRepository a, AssetInspectionRepository i,
            InspectionFindingRepository f, EventPublisher p, UnitOfWork u, DomainClock c) {
        super(a, i, f, p, u, c);
    }

    @Override
    public Uni<Result<AssetInspectionReadModel>> handle(StartAssetInspectionCommand cmd) {
        return requireInspection(cmd.tenantId(), AssetInspectionId.of(cmd.inspectionId()))
                .flatMap(inspection -> {
                    inspection.start(clock);
                    return save(inspection);
                })
                .map(saved -> Result.success(AssetInspectionReadModel.from(saved)));
    }
}
