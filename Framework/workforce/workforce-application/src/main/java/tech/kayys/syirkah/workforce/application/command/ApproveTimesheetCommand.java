package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.timesheet.TimesheetId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Objects;

/**
 * Command to approve a submitted timesheet.
 */
public record ApproveTimesheetCommand(
        TimesheetId timesheetId,
        WorkerId approverId,
        String approverActor,
        String notes
) implements Command {
    public ApproveTimesheetCommand {
        Objects.requireNonNull(timesheetId, "timesheetId cannot be null");
        Objects.requireNonNull(approverId, "approverId cannot be null");
        Objects.requireNonNull(approverActor, "approverActor cannot be null");
    }
}
