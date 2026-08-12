package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseWaveService;
import tech.kayys.erp.warehouse.application.api.command.AddTaskToWaveCommand;
import tech.kayys.erp.warehouse.application.api.command.CompleteWaveTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateWaveCommand;
import tech.kayys.erp.warehouse.domain.identifier.WaveId;
import tech.kayys.erp.warehouse.domain.valueobject.WaveType;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for wave management.
 */
@Path("/api/v1/warehouses/{warehouseId}/waves")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Wave API", description = "Warehouse wave management endpoints")
public class WaveResource {

    @Inject
    WarehouseWaveService waveService;

    @POST
    @Operation(summary = "Create a wave")
    @APIResponse(responseCode = "201", description = "Wave created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createWave(
            @PathParam("warehouseId") UUID warehouseId,
            @Valid CreateWaveRequest request) {
        CreateWaveCommand command = CreateWaveCommand.builder()
            .warehouseId(warehouseId)
            .waveType(request.getWaveType())
            .scheduledStartTime(request.getScheduledStartTime() != null ? 
                request.getScheduledStartTime() : Instant.now().plusSeconds(3600))
            .scheduledEndTime(request.getScheduledEndTime())
            .priority(request.getPriority())
            .zone(request.getZone())
            .assignedTo(request.getAssignedTo())
            .notes(request.getNotes())
            .build();

        return waveService.createWave(command)
            .thenApply(waveId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId + "/waves/" + waveId.getValue()))
                .entity(new CreateWaveResponse(waveId))
                .build()
            )
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/{waveId}")
    @Operation(summary = "Get wave by ID")
    @APIResponse(responseCode = "200", description = "Wave found")
    @APIResponse(responseCode = "404", description = "Wave not found")
    public CompletionStage<Response> getWave(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("waveId") UUID waveId) {
        WaveId id = WaveId.of(waveId);
        return waveService.getWave(id)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{waveId}/tasks")
    @Operation(summary = "Add task to wave")
    @APIResponse(responseCode = "200", description = "Task added")
    @APIResponse(responseCode = "400", description = "Invalid request")
    @APIResponse(responseCode = "404", description = "Wave not found")
    public CompletionStage<Response> addTask(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("waveId") UUID waveId,
            @Valid AddTaskRequest request) {
        WaveId id = WaveId.of(waveId);

        AddTaskToWaveCommand command = AddTaskToWaveCommand.builder()
            .waveId(id)
            .taskId(request.getTaskId())
            .taskType(request.getTaskType())
            .taskReference(request.getTaskReference())
            .build();

        return waveService.addTaskToWave(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{waveId}/plan")
    @Operation(summary = "Plan wave")
    @APIResponse(responseCode = "200", description = "Wave planned")
    @APIResponse(responseCode = "404", description = "Wave not found")
    public CompletionStage<Response> planWave(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("waveId") UUID waveId) {
        WaveId id = WaveId.of(waveId);

        return waveService.planWave(id)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{waveId}/start")
    @Operation(summary = "Start wave")
    @APIResponse(responseCode = "200", description = "Wave started")
    @APIResponse(responseCode = "404", description = "Wave not found")
    public CompletionStage<Response> startWave(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("waveId") UUID waveId) {
        WaveId id = WaveId.of(waveId);

        return waveService.startWave(id)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/{waveId}/tasks/{taskId}/complete")
    @Operation(summary = "Complete wave task")
    @APIResponse(responseCode = "200", description = "Task completed")
    @APIResponse(responseCode = "400", description = "Invalid completion")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> completeTask(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("waveId") UUID waveId,
            @PathParam("taskId") String taskId,
            @Valid CompleteTaskRequest request) {
        WaveId id = WaveId.of(waveId);

        CompleteWaveTaskCommand command = CompleteWaveTaskCommand.builder()
            .waveId(id)
            .taskId(taskId)
            .completedBy(request.getCompletedBy())
            .build();

        return waveService.completeWaveTask(command)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                if (throwable.getCause() instanceof IllegalStateException) {
                    return Response.status(Response.Status.CONFLICT)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/metrics")
    @Operation(summary = "Get wave metrics")
    @APIResponse(responseCode = "200", description = "Wave metrics")
    public CompletionStage<Response> getMetrics(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate) {
        Instant start = startDate != null ? Instant.parse(startDate) : Instant.now().minusSeconds(7L * 24L * 60L * 60L);
        Instant end = endDate != null ? Instant.parse(endDate) : Instant.now();

        return waveService.getWaveMetrics(warehouseId, start, end)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateWaveRequest {
        private WaveType waveType;
        private Instant scheduledStartTime;
        private Instant scheduledEndTime;
        private Integer priority;
        private String zone;
        private String assignedTo;
        private String notes;

        public WaveType getWaveType() { return waveType; }
        public void setWaveType(WaveType waveType) { this.waveType = waveType; }
        public Instant getScheduledStartTime() { return scheduledStartTime; }
        public void setScheduledStartTime(Instant scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }
        public Instant getScheduledEndTime() { return scheduledEndTime; }
        public void setScheduledEndTime(Instant scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; }
        public Integer getPriority() { return priority; }
        public void setPriority(Integer priority) { this.priority = priority; }
        public String getZone() { return zone; }
        public void setZone(String zone) { this.zone = zone; }
        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class AddTaskRequest {
        private String taskId;
        private String taskType;
        private String taskReference;

        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getTaskType() { return taskType; }
        public void setTaskType(String taskType) { this.taskType = taskType; }
        public String getTaskReference() { return taskReference; }
        public void setTaskReference(String taskReference) { this.taskReference = taskReference; }
    }

    public static class CompleteTaskRequest {
        private String completedBy;

        public String getCompletedBy() { return completedBy; }
        public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
    }

    public static class CreateWaveResponse {
        private final String waveId;

        public CreateWaveResponse(WaveId waveId) {
            this.waveId = waveId.toString();
        }

        public String getWaveId() { return waveId; }
    }
}