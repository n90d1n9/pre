package tech.kayys.syirkah.asset.application.installation;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.installation.AssetInstallation;
import tech.kayys.syirkah.asset.domain.repository.AssetInstallationRepository;

import java.util.List;
import java.util.Objects;

public class GetInstallationHistoryHandler {

    private final AssetInstallationRepository installations;

    public GetInstallationHistoryHandler(AssetInstallationRepository installations) {
        this.installations = Objects.requireNonNull(installations, "installations");
    }

    public Uni<List<AssetInstallation>> handle(GetInstallationHistoryQuery query) {
        return Uni.createFrom().completionStage(() -> installations.findHistory(query.tenantId(), query.assetId()));
    }
}
