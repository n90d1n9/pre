package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

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
