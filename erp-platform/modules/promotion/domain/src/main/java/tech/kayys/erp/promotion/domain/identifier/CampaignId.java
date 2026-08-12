package tech.kayys.erp.promotion.domain.identifier;

import tech.kayys.erp.foundation.domain.Identifier;

import java.util.UUID;

/**
 * Marketing campaign identifier.
 */
public final class CampaignId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public CampaignId(UUID value) {
        super(value);
    }

    public static CampaignId of(UUID value) {
        return new CampaignId(value);
    }

    public static CampaignId generate() {
        return new CampaignId(UUID.randomUUID());
    }

    public static CampaignId fromString(String value) {
        return new CampaignId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "CampaignId{" + value + "}";
    }
}