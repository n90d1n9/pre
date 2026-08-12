package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseReceivingService;
import tech.kayys.erp.warehouse.application.api.command.CompleteQualityCheckCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateReceivingTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.ReceiveItemCommand;
import tech.kayys.erp.warehouse.application.api.query.ReceivingTaskView;
import tech.kayys.erp.warehouse.domain.identifier.ReceivingTaskId;
import tech.kayys.erp.warehouse.domain.valueobject.QualityCheckResult;

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
 * REST API for receiving operations.
 */
@Path("/api/v1/warehouses/{warehouseId}/receiving")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Receiving API", description = "Warehouse receiving management endpoints")
public class ReceivingResource {

    @Inject
    WarehouseReceivingService receivingService;

    @POST
    @Path("/tasks")
    @Operation(summary = "Create a receiving task")
    @APIResponse(responseCode = "201", description = "Receiving task created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createReceivingTask(
            @PathParam("warehouseId") UUID warehouseId,
            @Valid CreateReceivingTaskRequest request) {
        CreateReceivingTaskCommand command = CreateReceivingTaskCommand.builder()
            .warehouseId(warehouseId)
            .purchaseOrderNumber(request.getPurchaseOrderNumber())
            .supplierName(request.getSupplierName())
            .expectedDate(request.getExpectedDate() != null ? request.getExpectedDate() : Instant.now().plusSeconds(7L * 24L * 60L * 60L))
            .carrierName(request.getCarrierName())
            .trackingNumber(request.getTrackingNumber())
            .receivingLocation(request.getReceivingLocation())
            .items(request.getItems())
            .notes(request.getNotes())
            .build();

        return receivingService.createReceivingTask(command)
            .thenApply(taskId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId + "/receiving/tasks/" + taskId.getValue()))
                .entity(new CreateReceivingTaskResponse(taskId))
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
    @Operation(summary = "Get receiving task by ID")
    @APIResponse(responseCode = "200", description = "Task found")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> getReceivingTask(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ReceivingTaskId id = ReceivingTaskId.of(taskId);
        return receivingService.getReceivingTask(id)
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
    @Path("/tasks/{taskId}/arrive")
    @Operation(summary = "Mark shipment as arrived")
    @APIResponse(responseCode = "200", description = "Shipment marked as arrived")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> markArrived(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ReceivingTaskId id = ReceivingTaskId.of(taskId);

        return receivingService.markArrived(id)
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
    @Path("/tasks/{taskId}/quality/start")
    @Operation(summary = "Start quality check")
    @APIResponse(responseCode = "200", description = "Quality check started")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> startQualityCheck(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ReceivingTaskId id = ReceivingTaskId.of(taskId);

        return receivingService.startQualityCheck(id)
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
    @Path("/tasks/{taskId}/quality/complete")
    @Operation(summary = "Complete quality check")
    @APIResponse(responseCode = "200", description = "Quality check completed")
    @APIResponse(responseCode = "400", description = "Invalid quality result")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> completeQualityCheck(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId,
            @Valid CompleteQualityCheckRequest request) {
        ReceivingTaskId id = ReceivingTaskId.of(taskId);

        CompleteQualityCheckCommand command = CompleteQualityCheckCommand.builder()
            .taskId(id)
            .result(request.getResult())
            .checkedBy(request.getCheckedBy())
            .notes(request.getNotes())
            .build();

        return receivingService.completeQualityCheck(command)
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
    @Path("/tasks/{taskId}/items")
    @Operation(summary = "Receive an item")
    @APIResponse(responseCode = "200", description = "Item received")
    @APIResponse(responseCode = "400", description = "Invalid receive")
    @APIResponse(responseCode = "404", description = "Item not found")
    public CompletionStage<Response> receiveItem(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId,
            @Valid ReceiveItemRequest request) {
        ReceivingTaskId id = ReceivingTaskId.of(taskId);

        ReceiveItemCommand command = ReceiveItemCommand.builder()
            .taskId(id)
            .itemId(request.getItemId())
            .quantity(request.getQuantity())
            .receivedBy(request.getReceivedBy())
            .binLocationId(request.getBinLocationId())
            .build();

        return receivingService.receiveItem(command)
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
    @Operation(summary = "Complete receiving")
    @APIResponse(responseCode = "200", description = "Receiving completed")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> completeReceiving(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ReceivingTaskId id = ReceivingTaskId.of(taskId);

        return receivingService.completeReceivingTask(id)
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
    @Operation(summary = "Get receiving tasks")
    @APIResponse(responseCode = "200", description = "Tasks found")
    public CompletionStage<Response> getTasks(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return receivingService.getReceivingTasksByWarehouse(warehouseId, status, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateReceivingTaskRequest {
        private String purchaseOrderNumber;
        private String supplierName;
        private Instant expectedDate;
        private String carrierName;
        private String trackingNumber;
        private String receivingLocation;
        private List<CreateReceivingTaskCommand.ReceivingItemCommand> items;
        private String notes;

        public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
        public void setPurchaseOrderNumber(String purchaseOrderNumber) { this.purchaseOrderNumber = purchaseOrderNumber; }
        public String getSupplierName() { return supplierName; }
        public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
        public Instant getExpectedDate() { return expectedDate; }
        public void setExpectedDate(Instant expectedDate) { this.expectedDate = expectedDate; }
        public String getCarrierName() { return carrierName; }
        public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getReceivingLocation() { return receivingLocation; }
        public void setReceivingLocation(String receivingLocation) { this.receivingLocation = receivingLocation; }
        public List<CreateReceivingTaskCommand.ReceivingItemCommand> getItems() { return items; }
        public void setItems(List<CreateReceivingTaskCommand.ReceivingItemCommand> items) { this.items = items; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class CompleteQualityCheckRequest {
        private QualityCheckResult result;
        private String checkedBy;
        private String notes;

        public QualityCheckResult getResult() { return result; }
        public void setResult(QualityCheckResult result) { this.result = result; }
        public String getCheckedBy() { return checkedBy; }
        public void setCheckedBy(String checkedBy) { this.checkedBy = checkedBy; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class ReceiveItemRequest {
        private String itemId;
        private int quantity;
        private String receivedBy;
        private String binLocationId;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getReceivedBy() { return receivedBy; }
        public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
        public String getBinLocationId() { return binLocationId; }
        public void setBinLocationId(String binLocationId) { this.binLocationId = binLocationId; }
    }

    public static class CreateReceivingTaskResponse {
        private final String taskId;

        public CreateReceivingTaskResponse(ReceivingTaskId taskId) {
            this.taskId = taskId.toString();
        }

        public String getTaskId() { return taskId; }
    }
}