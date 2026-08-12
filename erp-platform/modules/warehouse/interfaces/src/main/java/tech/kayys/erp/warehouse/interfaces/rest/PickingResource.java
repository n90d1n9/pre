package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehousePickingService;
import tech.kayys.erp.warehouse.application.api.command.AssignPickerCommand;
import tech.kayys.erp.warehouse.application.api.command.CreatePickListCommand;
import tech.kayys.erp.warehouse.application.api.command.PickItemCommand;
import tech.kayys.erp.warehouse.domain.identifier.PickListId;
import tech.kayys.erp.warehouse.domain.valueobject.PickStrategy;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for picking operations.
 */
@Path("/api/v1/warehouses/{warehouseId}/picking")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Picking API", description = "Warehouse picking management endpoints")
public class PickingResource {

    @Inject
    WarehousePickingService pickingService;

    @POST
    @Path("/picklists")
    @Operation(summary = "Create a pick list")
    @APIResponse(responseCode = "201", description = "Pick list created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createPickList(
            @PathParam("warehouseId") UUID warehouseId,
            @Valid CreatePickListRequest request) {
        CreatePickListCommand command = CreatePickListCommand.builder()
            .warehouseId(warehouseId)
            .sourceReference(request.getSourceReference())
            .sourceType(request.getSourceType())
            .strategy(request.getStrategy() != null ? request.getStrategy() : PickStrategy.FIFO)
            .priority(request.getPriority())
            .waveNumber(request.getWaveNumber())
            .zone(request.getZone())
            .items(request.getItems())
            .notes(request.getNotes())
            .build();

        return pickingService.createPickList(command)
            .thenApply(pickListId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId + "/picking/picklists/" + pickListId.getValue()))
                .entity(new CreatePickListResponse(pickListId))
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
    @Path("/picklists/{pickListId}")
    @Operation(summary = "Get pick list by ID")
    @APIResponse(responseCode = "200", description = "Pick list found")
    @APIResponse(responseCode = "404", description = "Pick list not found")
    public CompletionStage<Response> getPickList(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("pickListId") UUID pickListId) {
        PickListId id = PickListId.of(pickListId);
        return pickingService.getPickList(id)
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
    @Path("/picklists/{pickListId}/assign")
    @Operation(summary = "Assign picker to pick list")
    @APIResponse(responseCode = "200", description = "Picker assigned")
    @APIResponse(responseCode = "400", description = "Invalid assignment")
    @APIResponse(responseCode = "404", description = "Pick list not found")
    public CompletionStage<Response> assignPicker(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("pickListId") UUID pickListId,
            @Valid AssignPickerRequest request) {
        PickListId id = PickListId.of(pickListId);

        AssignPickerCommand command = AssignPickerCommand.builder()
            .pickListId(id)
            .pickerId(request.getPickerId())
            .build();

        return pickingService.assignPicker(command)
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
    @Path("/picklists/{pickListId}/start")
    @Operation(summary = "Start picking")
    @APIResponse(responseCode = "200", description = "Picking started")
    @APIResponse(responseCode = "404", description = "Pick list not found")
    public CompletionStage<Response> startPicking(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("pickListId") UUID pickListId) {
        PickListId id = PickListId.of(pickListId);

        return pickingService.startPickList(id)
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
    @Path("/picklists/{pickListId}/items")
    @Operation(summary = "Pick an item")
    @APIResponse(responseCode = "200", description = "Item picked")
    @APIResponse(responseCode = "400", description = "Invalid pick")
    @APIResponse(responseCode = "404", description = "Item not found")
    public CompletionStage<Response> pickItem(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("pickListId") UUID pickListId,
            @Valid PickItemRequest request) {
        PickListId id = PickListId.of(pickListId);

        PickItemCommand command = PickItemCommand.builder()
            .pickListId(id)
            .itemId(request.getItemId())
            .quantity(request.getQuantity())
            .pickedBy(request.getPickedBy())
            .binLocation(request.getBinLocation())
            .build();

        return pickingService.pickItem(command)
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
    @Path("/picklists/{pickListId}/complete")
    @Operation(summary = "Complete picking")
    @APIResponse(responseCode = "200", description = "Picking completed")
    @APIResponse(responseCode = "404", description = "Pick list not found")
    public CompletionStage<Response> completePicking(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("pickListId") UUID pickListId) {
        PickListId id = PickListId.of(pickListId);

        return pickingService.completePickList(id)
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
    @Path("/picklists")
    @Operation(summary = "Get pick lists")
    @APIResponse(responseCode = "200", description = "Pick lists found")
    public CompletionStage<Response> getPickLists(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return pickingService.getPickListsByWarehouse(warehouseId, status, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreatePickListRequest {
        private String sourceReference;
        private String sourceType;
        private PickStrategy strategy;
        private String priority;
        private String waveNumber;
        private String zone;
        private List<CreatePickListCommand.PickItemCommand> items;
        private String notes;

        public String getSourceReference() { return sourceReference; }
        public void setSourceReference(String sourceReference) { this.sourceReference = sourceReference; }
        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }
        public PickStrategy getStrategy() { return strategy; }
        public void setStrategy(PickStrategy strategy) { this.strategy = strategy; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getWaveNumber() { return waveNumber; }
        public void setWaveNumber(String waveNumber) { this.waveNumber = waveNumber; }
        public String getZone() { return zone; }
        public void setZone(String zone) { this.zone = zone; }
        public List<CreatePickListCommand.PickItemCommand> getItems() { return items; }
        public void setItems(List<CreatePickListCommand.PickItemCommand> items) { this.items = items; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class AssignPickerRequest {
        private String pickerId;

        public String getPickerId() { return pickerId; }
        public void setPickerId(String pickerId) { this.pickerId = pickerId; }
    }

    public static class PickItemRequest {
        private String itemId;
        private int quantity;
        private String pickedBy;
        private String binLocation;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getPickedBy() { return pickedBy; }
        public void setPickedBy(String pickedBy) { this.pickedBy = pickedBy; }
        public String getBinLocation() { return binLocation; }
        public void setBinLocation(String binLocation) { this.binLocation = binLocation; }
    }

    public static class CreatePickListResponse {
        private final String pickListId;

        public CreatePickListResponse(PickListId pickListId) {
            this.pickListId = pickListId.toString();
        }

        public String getPickListId() { return pickListId; }
    }
}