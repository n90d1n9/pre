package tech.kayys.syirkah.crm.application.api.command;

import tech.kayys.syirkah.crm.domain.referral.ReferralId;
import tech.kayys.syirkah.foundation.application.command.Command;
import java.util.Objects;
import java.util.UUID;

/**
 * Command to convert a referral to a lead or opportunity.
 */
public record ConvertReferralCommand(
        ReferralId referralId,
        UUID leadId,
        UUID opportunityId
) implements Command {

    public ConvertReferralCommand {
        Objects.requireNonNull(referralId, "referralId cannot be null");
        // At least one of leadId or opportunityId should be provided
        if (leadId == null && opportunityId == null) {
            throw new IllegalArgumentException("Either leadId or opportunityId must be provided");
        }
    }
}