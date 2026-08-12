package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehousePutawayService;
import tech.kayys.erp.warehouse.application.api.command.AssignPutawayTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.CompletePutawayItemCommand;
import tech.kayys.erp.warehouse.application.api.command.CreatePutawayTaskCommand;
import tech.kayys.erp.warehouse.domain.identifier.PutawayTaskId;
import tech.kayys.erp.warehouse.domain.valueobject.PutawayStrategy;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for putaway operations.
 */
@Path("/api/v1/warehouses/{warehouseId}/putaway")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Putaway API", description = "Warehouse putaway management endpoints")
public class PutawayResource {

    @Inject
    WarehousePutawayService putawayService;

    @POST
    @Path("/tasks")
    @Operation(summary = "Create a putaway task")
    @APIResponse(responseCode = "201", description = "Putaway task created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createPutawayTask(
            @PathParam("warehouseId") UUID warehouseId,
            @Valid CreatePutawayTaskRequest request) {
        CreatePutawayTaskCommand command = CreatePutawayTaskCommand.builder()
            .warehouseId(warehouseId)
            .receivingReference(request.getReceivingReference())
            .receivingType(request.getReceivingType())
            .strategy(request.getStrategy() != null ? request.getStrategy() : PutawayStrategy.NEAREST)
            .zone(request.getZone())
            .items(request.getItems())
            .notes(request.getNotes())
            .build();

        return putawayService.createPutawayTask(command)
            .thenApply(taskId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId + "/putaway/tasks/" + taskId.getValue()))
                .entity(new CreatePutawayTaskResponse(taskId))
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
    @Path("/tasks/{taskId}")
    @Operation(summary = "Get putaway task by ID")
    @APIResponse(responseCode = "200", description = "Task found")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> getPutawayTask(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        PutawayTaskId id = PutawayTaskId.of(taskId);
        return putawayService.getPutawayTask(id)
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
    @Path("/tasks/{taskId}/assign")
    @Operation(summary = "Assign worker to putaway task")
    @APIResponse(responseCode = "200", description = "Worker assigned")
    @APIResponse(responseCode = "400", description = "Invalid assignment")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> assignWorker(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId,
            @Valid AssignWorkerRequest request) {
        PutawayTaskId id = PutawayTaskId.of(taskId);

        AssignPutawayTaskCommand command = AssignPutawayTaskCommand.builder()
            .taskId(id)
            .assignedTo(request.getAssignedTo())
            .build();

        return putawayService.assignPutawayTask(command)
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
    @Path("/tasks/{taskId}/start")
    @Operation(summary = "Start putaway")
    @APIResponse(responseCode = "200", description = "Putaway started")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> startPutaway(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        PutawayTaskId id = PutawayTaskId.of(taskId);

        return putawayService.startPutawayTask(id)
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
    @Path("/tasks/{taskId}/items")
    @Operation(summary = "Complete a putaway item")
    @APIResponse(responseCode = "200", description = "Item completed")
    @APIResponse(responseCode = "400", description = "Invalid completion")
    @APIResponse(responseCode = "404", description = "Item not found")
    public CompletionStage<Response> completeItem(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId,
            @Valid CompletePutawayItemRequest request) {
        PutawayTaskId id = PutawayTaskId.of(taskId);

        CompletePutawayItemCommand command = CompletePutawayItemCommand.builder()
            .taskId(id)
            .itemId(request.getItemId())
            .binLocationId(request.getBinLocationId())
            .completedBy(request.getCompletedBy())
            .build();

        return putawayService.completePutawayItem(command)
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
    @Path("/tasks/{taskId}/complete")
    @Operation(summary = "Complete putaway")
    @APIResponse(responseCode = "200", description = "Putaway completed")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> completePutaway(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        PutawayTaskId id = PutawayTaskId.of(taskId);

        return putawayService.completePutawayTask(id)
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

    @GET
    @Path("/tasks")
    @Operation(summary = "Get putaway tasks")
    @APIResponse(responseCode = "200", description = "Tasks found")
    public CompletionStage<Response> getTasks(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return putawayService.getPutawayTasksByWarehouse(warehouseId, status, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/suggest-bin")
    @Operation(summary = "Suggest optimal bin for putaway")
    @APIResponse(responseCode = "200", description = "Bin suggestion")
    public CompletionStage<Response> suggestBin(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("productId") String productId,
            @QueryParam("quantity") int quantity) {
        return putawayService.suggestBinForPutaway(productId, quantity, warehouseId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreatePutawayTaskRequest {
        private String receivingReference;
        private String receivingType;
        private PutawayStrategy strategy;
        private String zone;
        private List<CreatePutawayTaskCommand.PutawayItemCommand> items;
        private String notes;

        public String getReceivingReference() { return receivingReference; }
        public void setReceivingReference(String receivingReference) { this.receivingReference = receivingReference; }
        public String getReceivingType() { return receivingType; }
        public void setReceivingType(String receivingType) { this.receivingType = receivingType; }
        public PutawayStrategy getStrategy() { return strategy; }
        public void setStrategy(PutawayStrategy strategy) { this.strategy = strategy; }
        public String getZone() { return zone; }
        public void setZone(String zone) { this.zone = zone; }
        public List<CreatePutawayTaskCommand.PutawayItemCommand> getItems() { return items; }
        public void setItems(List<CreatePutawayTaskCommand.PutawayItemCommand> items) { this.items = items; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class AssignWorkerRequest {
        private String assignedTo;

        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    }

    public static class CompletePutawayItemRequest {
        private String itemId;
        private String binLocationId;
        private String completedBy;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public String getBinLocationId() { return binLocationId; }
        public void setBinLocationId(String binLocationId) { this.binLocationId = binLocationId; }
        public String getCompletedBy() { return completedBy; }
        public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
    }

    public static class CreatePutawayTaskResponse {
        private final String taskId;

        public CreatePutawayTaskResponse(PutawayTaskId taskId) {
            this.taskId = taskId.toString();
        }

        public String getTaskId() { return taskId; }
    }
}