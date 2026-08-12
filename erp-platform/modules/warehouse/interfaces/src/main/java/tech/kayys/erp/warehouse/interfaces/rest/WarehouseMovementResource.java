package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseMovementService;
import tech.kayys.erp.warehouse.application.api.command.CompleteInventoryMovementCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateInventoryMovementCommand;
import tech.kayys.erp.warehouse.domain.identifier.InventoryMovementId;
import tech.kayys.erp.warehouse.domain.valueobject.MovementType;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for inventory movement operations.
 */
@Path("/api/v1/warehouses/movements")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Inventory Movement API", description = "Inventory movement management endpoints")
public class WarehouseMovementResource {

    @Inject
    WarehouseMovementService movementService;

    @POST
    @Operation(summary = "Create an inventory movement")
    @APIResponse(responseCode = "201", description = "Movement created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createMovement(@Valid CreateInventoryMovementRequest request) {
        CreateInventoryMovementCommand command = CreateInventoryMovementCommand.builder()
            .sourceWarehouseId(request.getSourceWarehouseId())
            .destinationWarehouseId(request.getDestinationWarehouseId())
            .movementType(request.getMovementType())
            .createdBy(request.getCreatedBy())
            .reason(request.getReason())
            .referenceNumber(request.getReferenceNumber())
            .items(request.getItems())
            .notes(request.getNotes())
            .build();

        return movementService.createInventoryMovement(command)
            .thenApply(movementId -> Response
                .created(URI.create("/api/v1/warehouses/movements/" + movementId.getValue()))
                .entity(new CreateMovementResponse(movementId))
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
    @Path("/{movementId}")
    @Operation(summary = "Get movement by ID")
    @APIResponse(responseCode = "200", description = "Movement found")
    @APIResponse(responseCode = "404", description = "Movement not found")
    public CompletionStage<Response> getMovement(@PathParam("movementId") UUID movementId) {
        InventoryMovementId id = InventoryMovementId.of(movementId);
        return movementService.getInventoryMovement(id)
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
    @Path("/{movementId}/start")
    @Operation(summary = "Start inventory movement")
    @APIResponse(responseCode = "200", description = "Movement started")
    @APIResponse(responseCode = "404", description = "Movement not found")
    public CompletionStage<Response> startMovement(@PathParam("movementId") UUID movementId) {
        InventoryMovementId id = InventoryMovementId.of(movementId);

        return movementService.startMovement(id)
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
    @Path("/{movementId}/complete")
    @Operation(summary = "Complete inventory movement")
    @APIResponse(responseCode = "200", description = "Movement completed")
    @APIResponse(responseCode = "404", description = "Movement not found")
    public CompletionStage<Response> completeMovement(
            @PathParam("movementId") UUID movementId,
            @Valid CompleteMovementRequest request) {
        InventoryMovementId id = InventoryMovementId.of(movementId);

        CompleteInventoryMovementCommand command = CompleteInventoryMovementCommand.builder()
            .movementId(id)
            .completedBy(request.getCompletedBy())
            .build();

        return movementService.completeMovement(command)
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
    @Path("/{movementId}/cancel")
    @Operation(summary = "Cancel inventory movement")
    @APIResponse(responseCode = "200", description = "Movement cancelled")
    @APIResponse(responseCode = "404", description = "Movement not found")
    public CompletionStage<Response> cancelMovement(
            @PathParam("movementId") UUID movementId,
            @Valid CancelMovementRequest request) {
        InventoryMovementId id = InventoryMovementId.of(movementId);

        return movementService.cancelMovement(id, request.getReason())
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
    @Path("/search")
    @Operation(summary = "Search inventory movements")
    @APIResponse(responseCode = "200", description = "Search results")
    public CompletionStage<Response> searchMovements(
            @QueryParam("warehouseId") UUID warehouseId,
            @QueryParam("status") String status,
            @QueryParam("type") String type,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return movementService.getMovementsByWarehouse(warehouseId, status, type, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateInventoryMovementRequest {
        private UUID sourceWarehouseId;
        private UUID destinationWarehouseId;
        private MovementType movementType;
        private String createdBy;
        private String reason;
        private String referenceNumber;
        private List<CreateInventoryMovementCommand.MovementItemCommand> items;
        private String notes;

        public UUID getSourceWarehouseId() { return sourceWarehouseId; }
        public void setSourceWarehouseId(UUID sourceWarehouseId) { this.sourceWarehouseId = sourceWarehouseId; }
        public UUID getDestinationWarehouseId() { return destinationWarehouseId; }
        public void setDestinationWarehouseId(UUID destinationWarehouseId) { this.destinationWarehouseId = destinationWarehouseId; }
        public MovementType getMovementType() { return movementType; }
        public void setMovementType(MovementType movementType) { this.movementType = movementType; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getReferenceNumber() { return referenceNumber; }
        public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
        public List<CreateInventoryMovementCommand.MovementItemCommand> getItems() { return items; }
        public void setItems(List<CreateInventoryMovementCommand.MovementItemCommand> items) { this.items = items; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class CompleteMovementRequest {
        private String completedBy;

        public String getCompletedBy() { return completedBy; }
        public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
    }

    public static class CancelMovementRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class CreateMovementResponse {
        private final String movementId;

        public CreateMovementResponse(InventoryMovementId movementId) {
            this.movementId = movementId.toString();
        }

        public String getMovementId() { return movementId; }
    }
}