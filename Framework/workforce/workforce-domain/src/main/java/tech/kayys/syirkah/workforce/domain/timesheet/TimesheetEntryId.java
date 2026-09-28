package tech.kayys.syirkah.workforce.domain.timesheet;

import tech.kayys.syirkah.foundation.domain.identifier.DomainId;
import java.util.UUID;

public record TimesheetEntryId(UUID value) implements DomainId<UUID> {
    public static TimesheetEntryId of(UUID value) { return new TimesheetEntryId(value); }
    public static TimesheetEntryId generate() { return new TimesheetEntryId(UUID.randomUUID()); }
}
