package tech.kayys.syirkah.asset.application.api.command;

import tech.kayys.syirkah.asset.domain.identifier.AssetId;
import tech.kayys.syirkah.foundation.application.command.Command;

import java.time.LocalDate;

/**
 * Command to dispose an asset.
 */
public record DisposeAssetCommand(
        AssetId assetId,
        LocalDate disposalDate,
        String reason
) implements Command {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AssetId assetId;
        private LocalDate disposalDate;
        private String reason;

        public Builder assetId(AssetId assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder disposalDate(LocalDate disposalDate) {
            this.disposalDate = disposalDate;
            return this;
        }

        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public DisposeAssetCommand build() {
            return new DisposeAssetCommand(assetId, disposalDate, reason);
        }
    }
}
