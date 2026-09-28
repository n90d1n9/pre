package tech.kayys.syirkah.workforce.domain.timesheet;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;
import java.util.UUID;

public record TimesheetId(UUID value) implements DomainId<UUID> {
    public static TimesheetId of(UUID value) { return new TimesheetId(value); }
    public static TimesheetId generate() { return new TimesheetId(UUID.randomUUID()); }
}
