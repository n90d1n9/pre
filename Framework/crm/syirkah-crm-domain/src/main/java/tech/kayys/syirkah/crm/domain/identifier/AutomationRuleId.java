package tech.kayys.syirkah.crm.domain.identifier;

import tech.kayys.syirkah.foundation.domain.identifier.Identifier;

import java.util.UUID;

public final class AutomationRuleId extends Identifier<UUID> {
    
    private static final long serialVersionUID = 1L;

    public AutomationRuleId(UUID value) {
        super(value);
    }

    public static AutomationRuleId of(UUID value) {
        return new AutomationRuleId(value);
    }

    public static AutomationRuleId generate() {
        return new AutomationRuleId(UUID.randomUUID());
    }

    public static AutomationRuleId fromString(String value) {
        return new AutomationRuleId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return "AutomationRuleId{" + value + "}";
    }
}
