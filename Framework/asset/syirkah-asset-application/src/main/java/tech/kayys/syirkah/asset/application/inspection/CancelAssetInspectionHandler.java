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

public class CancelAssetInspectionHandler extends AbstractAssetInspectionHandler
        implements CommandHandler<CancelAssetInspectionCommand, Result<AssetInspectionReadModel>> {

    public CancelAssetInspectionHandler(AssetRepository a, AssetInspectionRepository i,
            InspectionFindingRepository f, EventPublisher p, UnitOfWork u, DomainClock c) {
        super(a, i, f, p, u, c);
    }

    @Override
    public Uni<Result<AssetInspectionReadModel>> handle(CancelAssetInspectionCommand cmd) {
        return requireInspection(cmd.tenantId(), AssetInspectionId.of(cmd.inspectionId()))
                .flatMap(inspection -> {
                    inspection.cancel(clock);
                    return save(inspection);
                })
                .map(saved -> Result.success(AssetInspectionReadModel.from(saved)));
    }
}
