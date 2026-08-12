package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseBinService;
import tech.kayys.erp.warehouse.application.api.command.AssignProductToBinCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateBinLocationCommand;
import tech.kayys.erp.warehouse.domain.identifier.BinLocationId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for bin location management.
 */
@Path("/api/v1/warehouses/{warehouseId}/bins")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Bin Location API", description = "Bin location management endpoints")
public class BinLocationResource {

    @Inject
    WarehouseBinService warehouseBinService;

    @POST
    @Operation(summary = "Create a new bin location")
    @APIResponse(responseCode = "201", description = "Bin created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    @APIResponse(responseCode = "409", description = "Bin code already exists")
    public CompletionStage<Response> createBin(
            @PathParam("warehouseId") UUID warehouseId,
            @Valid CreateBinRequest request) {
        CreateBinLocationCommand command = CreateBinLocationCommand.builder()
            .warehouseId(warehouseId)
            .code(request.getCode())
            .name(request.getName())
            .description(request.getDescription())
            .binType(request.getBinType())
            .zone(request.getZone())
            .aisle(request.getAisle())
            .level(request.getLevel())
            .position(request.getPosition())
            .capacity(request.getCapacity())
            .maxWeight(request.getMaxWeight())
            .maxLength(request.getMaxLength())
            .maxWidth(request.getMaxWidth())
            .maxHeight(request.getMaxHeight())
            .notes(request.getNotes())
            .build();

        return warehouseBinService.createBinLocation(command)
            .thenApply(binId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId + "/bins/" + binId.getValue()))
                .entity(new CreateBinResponse(binId))
                .build()
            )
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
    @Path("/{binId}")
    @Operation(summary = "Get bin location by ID")
    @APIResponse(responseCode = "200", description = "Bin found")
    @APIResponse(responseCode = "404", description = "Bin not found")
    public CompletionStage<Response> getBin(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("binId") UUID binId) {
        BinLocationId binLocationId = BinLocationId.of(binId);
        return warehouseBinService.getBinLocation(binLocationId)
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
    @Operation(summary = "Get all bins in warehouse")
    @APIResponse(responseCode = "200", description = "Bins found")
    public CompletionStage<Response> getBins(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("zone") String zone,
            @QueryParam("type") String type,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return warehouseBinService.getBins(warehouseId, zone, type, status, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    @Path("/{binId}/assign")
    @Operation(summary = "Assign a product to a bin")
    @APIResponse(responseCode = "200", description = "Product assigned")
    @APIResponse(responseCode = "400", description = "Invalid request")
    @APIResponse(responseCode = "404", description = "Bin not found")
    public CompletionStage<Response> assignProduct(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("binId") UUID binId,
            @Valid AssignProductRequest request) {
        BinLocationId binLocationId = BinLocationId.of(binId);

        AssignProductToBinCommand command = AssignProductToBinCommand.builder()
            .binLocationId(binLocationId)
            .productId(request.getProductId())
            .quantity(request.getQuantity())
            .build();

        return warehouseBinService.assignProductToBin(command)
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
    @Path("/{binId}/unassign")
    @Operation(summary = "Unassign a product from a bin")
    @APIResponse(responseCode = "200", description = "Product unassigned")
    @APIResponse(responseCode = "404", description = "Bin not found")
    public CompletionStage<Response> unassignProduct(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("binId") UUID binId,
            @Valid UnassignProductRequest request) {
        BinLocationId binLocationId = BinLocationId.of(binId);

        return warehouseBinService.unassignProductFromBin(binLocationId, request.getProductId())
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateBinRequest {
        private String code;
        private String name;
        private String description;
        private BinType binType;
        private String zone;
        private String aisle;
        private String level;
        private String position;
        private int capacity;
        private Integer maxWeight;
        private Integer maxLength;
        private Integer maxWidth;
        private Integer maxHeight;
        private String notes;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BinType getBinType() { return binType; }
        public void setBinType(BinType binType) { this.binType = binType; }
        public String getZone() { return zone; }
        public void setZone(String zone) { this.zone = zone; }
        public String getAisle() { return aisle; }
        public void setAisle(String aisle) { this.aisle = aisle; }
        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public Integer getMaxWeight() { return maxWeight; }
        public void setMaxWeight(Integer maxWeight) { this.maxWeight = maxWeight; }
        public Integer getMaxLength() { return maxLength; }
        public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
        public Integer getMaxWidth() { return maxWidth; }
        public void setMaxWidth(Integer maxWidth) { this.maxWidth = maxWidth; }
        public Integer getMaxHeight() { return maxHeight; }
        public void setMaxHeight(Integer maxHeight) { this.maxHeight = maxHeight; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class AssignProductRequest {
        private String productId;
        private int quantity;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class UnassignProductRequest {
        private String productId;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
    }

    public static class CreateBinResponse {
        private final String binId;

        public CreateBinResponse(BinLocationId binId) {
            this.binId = binId.toString();
        }

        public String getBinId() { return binId; }
    }
}