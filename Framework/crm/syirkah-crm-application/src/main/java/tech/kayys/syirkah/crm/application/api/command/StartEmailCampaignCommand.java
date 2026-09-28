package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.crm.domain.identifier.CampaignId;
import tech.kayys.syirkah.foundation.application.command.Command;

public record StartEmailCampaignCommand(
        CampaignId campaignId
) implements Command {
}
