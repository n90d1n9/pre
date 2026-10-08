package tech.kayys.syirkah.asset.application.inspection;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.ApplicationErrorException;
import tech.kayys.syirkah.foundation.application.result.Result;

public class GetAssetConditionHandler {

    private final AssetInspectionReadRepository readRepository;

    public GetAssetConditionHandler(AssetInspectionReadRepository readRepository) {
        this.readRepository = readRepository;
    }

    public Uni<Result<AssetConditionView>> handle(GetAssetConditionQuery query) {
        return Uni.createFrom()
                .completionStage(() -> readRepository.findCurrentCondition(query.tenantId(), query.assetId()))
                .map(opt -> opt.orElse(null))
                .onItem().ifNull().failWith(() -> new ApplicationErrorException(
                        ApplicationError.of("asset.condition-not-found",
                                "No completed inspection for asset: " + query.assetId())))
                .map(Result::success);
    }
}
