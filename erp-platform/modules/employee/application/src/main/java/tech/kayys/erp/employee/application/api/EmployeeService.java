package tech.kayys.erp.employee.application.api;

import tech.kayys.erp.employee.application.api.command.*;
import tech.kayys.erp.employee.application.api.query.EmployeeView;
import tech.kayys.erp.employee.application.api.query.LeaveRequestView;
import tech.kayys.erp.employee.domain.identifier.EmployeeId;
import tech.kayys.erp.employee.domain.identifier.LeaveRequestId;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Public API for employee and HR operations.
 */
public interface EmployeeService {

    // ============ Employee Commands ============

    /**
     * Creates a new employee.
     */
    CompletionStage<EmployeeId> createEmployee(CreateEmployeeCommand command);

    /**
     * Activates an employee.
     */
    CompletionStage<EmployeeId> activateEmployee(ActivateEmployeeCommand command);

    /**
     * Terminates an employee.
     */
    CompletionStage<EmployeeId> terminateEmployee(TerminateEmployeeCommand command);

    /**
     * Puts an employee on leave.
     */
    CompletionStage<EmployeeId> putEmployeeOnLeave(PutEmployeeOnLeaveCommand command);

    /**
     * Returns an employee from leave.
     */
    CompletionStage<EmployeeId> returnEmployeeFromLeave(ReturnEmployeeFromLeaveCommand command);

    /**
     * Promotes an employee.
     */
    CompletionStage<EmployeeId> promoteEmployee(PromoteEmployeeCommand command);

    /**
     * Transfers an employee.
     */
    CompletionStage<EmployeeId> transferEmployee(TransferEmployeeCommand command);

    /**
     * Adds a skill to an employee.
     */
    CompletionStage<EmployeeId> addEmployeeSkill(AddEmployeeSkillCommand command);

    // ============ Leave Commands ============

    /**
     * Creates a leave request.
     */
    CompletionStage<LeaveRequestId> createLeaveRequest(CreateLeaveRequestCommand command);

    /**
     * Approves a leave request.
     */
    CompletionStage<LeaveRequestId> approveLeaveRequest(ApproveLeaveRequestCommand command);

    /**
     * Rejects a leave request.
     */
    CompletionStage<LeaveRequestId> rejectLeaveRequest(RejectLeaveRequestCommand command);

    /**
     * Cancels a leave request.
     */
    CompletionStage<LeaveRequestId> cancelLeaveRequest(CancelLeaveRequestCommand command);

    // ============ Employee Queries ============

    /**
     * Gets an employee by ID.
     */
    CompletionStage<EmployeeView> getEmployee(EmployeeId employeeId);

    /**
     * Gets employees by department.
     */
    CompletionStage<List<EmployeeView>> getEmployeesByDepartment(DepartmentId departmentId);

    /**
     * Gets employees by position.
     */
    CompletionStage<List<EmployeeView>> getEmployeesByPosition(PositionId positionId);

    /**
     * Gets employees by manager.
     */
    CompletionStage<List<EmployeeView>> getEmployeesByManager(EmployeeId managerId);

    /**
     * Gets an employee's leave requests.
     */
    CompletionStage<List<LeaveRequestView>> getLeaveRequests(EmployeeId employeeId);

    /**
     * Gets an employee's leave balance.
     */
    CompletionStage<LeaveBalanceView> getLeaveBalance(EmployeeId employeeId);

    /**
     * Searches employees.
     */
    CompletionStage<List<EmployeeView>> searchEmployees(SearchEmployeesQuery query);
}