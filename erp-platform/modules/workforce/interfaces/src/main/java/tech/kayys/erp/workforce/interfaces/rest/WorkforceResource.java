package tech.kayys.erp.workforce.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.workforce.application.api.WorkforceService;
import tech.kayys.erp.workforce.application.api.command.ClockInCommand;
import tech.kayys.erp.workforce.application.api.command.ClockOutCommand;
import tech.kayys.erp.workforce.domain.identifier.AttendanceId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for workforce management.
 */
@Path("/api/v1/workforce")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Workforce API", description = "Workforce management endpoints")
public class WorkforceResource {

    @Inject
    WorkforceService workforceService;

    @POST
    @Path("/clock-in")
    @Operation(summary = "Clock in an employee")
    @APIResponse(responseCode = "200", description = "Clocked in successfully")
    @APIResponse(responseCode = "400", description = "Invalid request")
    public CompletionStage<Response> clockIn(@Valid ClockInRequest request) {
        ClockInCommand command = ClockInCommand.builder()
            .employeeId(request.getEmployeeId())
            .employeeName(request.getEmployeeName())
            .employeeNumber(request.getEmployeeNumber())
            .clockInTime(request.getClockInTime() != null ? request.getClockInTime() : LocalTime.now())
            .location(request.getLocation())
            .shiftId(request.getShiftId())
            .shiftName(request.getShiftName())
            .department(request.getDepartment())
            .createdBy(request.getCreatedBy())
            .build();

        return workforceService.clockIn(command)
            .thenApply(attendanceId -> Response
                .ok(new ClockInResponse(attendanceId))
                .build()
            )
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/clock-out")
    @Operation(summary = "Clock out an employee")
    @APIResponse(responseCode = "200", description = "Clocked out successfully")
    @APIResponse(responseCode = "400", description = "Invalid request")
    @APIResponse(responseCode = "404", description = "Attendance record not found")
    public CompletionStage<Response> clockOut(@Valid ClockOutRequest request) {
        AttendanceId attendanceId = AttendanceId.of(request.getAttendanceId());
        
        ClockOutCommand command = ClockOutCommand.builder()
            .attendanceId(attendanceId)
            .clockOutTime(request.getClockOutTime() != null ? request.getClockOutTime() : LocalTime.now())
            .modifiedBy(request.getModifiedBy())
            .build();

        return workforceService.clockOut(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/attendance/{id}")
    @Operation(summary = "Get attendance record by ID")
    @APIResponse(responseCode = "200", description = "Attendance record found")
    @APIResponse(responseCode = "404", description = "Attendance record not found")
    public CompletionStage<Response> getAttendance(@PathParam("id") UUID id) {
        AttendanceId attendanceId = AttendanceId.of(id);
        return workforceService.getAttendance(attendanceId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/attendance/employee/{employeeId}")
    @Operation(summary = "Get attendance records for an employee")
    @APIResponse(responseCode = "200", description = "Attendance records found")
    public CompletionStage<Response> getEmployeeAttendance(
            @PathParam("employeeId") UUID employeeId,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return workforceService.getEmployeeAttendance(employeeId, startDate, endDate, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/attendance/today")
    @Operation(summary = "Get today's attendance")
    @APIResponse(responseCode = "200", description = "Today's attendance records")
    public CompletionStage<Response> getTodayAttendance() {
        return workforceService.getTodayAttendance()
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class ClockInRequest {
        private UUID employeeId;
        private String employeeName;
        private String employeeNumber;
        private LocalTime clockInTime;
        private String location;
        private String shiftId;
        private String shiftName;
        private String department;
        private String createdBy;

        public UUID getEmployeeId() { return employeeId; }
        public void setEmployeeId(UUID employeeId) { this.employeeId = employeeId; }
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getEmployeeNumber() { return employeeNumber; }
        public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
        public LocalTime getClockInTime() { return clockInTime; }
        public void setClockInTime(LocalTime clockInTime) { this.clockInTime = clockInTime; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getShiftId() { return shiftId; }
        public void setShiftId(String shiftId) { this.shiftId = shiftId; }
        public String getShiftName() { return shiftName; }
        public void setShiftName(String shiftName) { this.shiftName = shiftName; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }

    public static class ClockOutRequest {
        private UUID attendanceId;
        private LocalTime clockOutTime;
        private String modifiedBy;

        public UUID getAttendanceId() { return attendanceId; }
        public void setAttendanceId(UUID attendanceId) { this.attendanceId = attendanceId; }
        public LocalTime getClockOutTime() { return clockOutTime; }
        public void setClockOutTime(LocalTime clockOutTime) { this.clockOutTime = clockOutTime; }
        public String getModifiedBy() { return modifiedBy; }
        public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }
    }

    public static class ClockInResponse {
        private final String attendanceId;

        public ClockInResponse(AttendanceId attendanceId) {
            this.attendanceId = attendanceId.toString();
        }

        public String getAttendanceId() { return attendanceId; }
    }
}