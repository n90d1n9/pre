package tech.kayys.erp.asset.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.asset.application.api.AssetService;
import tech.kayys.erp.asset.application.api.command.CreateAssetCommand;
import tech.kayys.erp.asset.application.api.command.DisposeAssetCommand;
import tech.kayys.erp.asset.application.api.query.AssetView;
import tech.kayys.erp.asset.domain.identifier.AssetId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for asset management.
 */
@Path("/api/v1/assets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Asset API", description = "Asset management endpoints")
public class AssetResource {

    @Inject
    AssetService assetService;

    @POST
    @Operation(summary = "Create a new asset")
    @APIResponse(responseCode = "201", description = "Asset created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createAsset(@Valid CreateAssetRequest request) {
        CreateAssetCommand command = CreateAssetCommand.builder()
            .assetNumber(request.getAssetNumber())
            .name(request.getName())
            .description(request.getDescription())
            .assetType(request.getAssetType())
            .categoryId(request.getCategoryId())
            .purchasePrice(request.getPurchasePrice())
            .currencyCode(request.getCurrencyCode() != null ? request.getCurrencyCode() : "USD")
            .purchaseDate(request.getPurchaseDate())
            .supplier(request.getSupplier())
            .invoiceNumber(request.getInvoiceNumber())
            .purchaseOrderNumber(request.getPurchaseOrderNumber())
            .location(request.getLocation())
            .department(request.getDepartment())
            .assignedTo(request.getAssignedTo())
            .responsiblePerson(request.getResponsiblePerson())
            .usefulLifeYears(request.getUsefulLifeYears() != null ? request.getUsefulLifeYears() : 5)
            .depreciationMethod(request.getDepreciationMethod() != null ? request.getDepreciationMethod() : "STRAIGHT_LINE")
            .notes(request.getNotes())
            .build();

        return assetService.createAsset(command)
            .thenApply(assetId -> Response
                .created(URI.create("/api/v1/assets/" + assetId.getValue()))
                .entity(new CreateAssetResponse(assetId))
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
    @Path("/{id}")
    @Operation(summary = "Get asset by ID")
    @APIResponse(responseCode = "200", description = "Asset found")
    @APIResponse(responseCode = "404", description = "Asset not found")
    public CompletionStage<Response> getAsset(@PathParam("id") UUID id) {
        AssetId assetId = AssetId.of(id);
        return assetService.getAsset(assetId)
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
    @Path("/{id}/dispose")
    @Operation(summary = "Dispose an asset")
    @APIResponse(responseCode = "200", description = "Asset disposed")
    @APIResponse(responseCode = "400", description = "Invalid request")
    @APIResponse(responseCode = "404", description = "Asset not found")
    public CompletionStage<Response> disposeAsset(
            @PathParam("id") UUID id,
            @Valid DisposeAssetRequest request) {
        AssetId assetId = AssetId.of(id);

        DisposeAssetCommand command = DisposeAssetCommand.builder()
            .assetId(assetId)
            .disposalDate(request.getDisposalDate())
            .reason(request.getReason())
            .build();

        return assetService.disposeAsset(command)
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
    @Path("/search")
    @Operation(summary = "Search assets")
    @APIResponse(responseCode = "200", description = "Search results")
    public CompletionStage<Response> searchAssets(
            @QueryParam("status") String status,
            @QueryParam("type") String type,
            @QueryParam("department") String department,
            @QueryParam("assignedTo") String assignedTo,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return assetService.searchAssets(status, type, department, assignedTo, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateAssetRequest {
        private String assetNumber;
        private String name;
        private String description;
        private AssetType assetType;
        private UUID categoryId;
        private String purchasePrice;
        private String currencyCode;
        private LocalDate purchaseDate;
        private String supplier;
        private String invoiceNumber;
        private String purchaseOrderNumber;
        private String location;
        private String department;
        private String assignedTo;
        private String responsiblePerson;
        private Integer usefulLifeYears;
        private String depreciationMethod;
        private String notes;

        public String getAssetNumber() { return assetNumber; }
        public void setAssetNumber(String assetNumber) { this.assetNumber = assetNumber; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public AssetType getAssetType() { return assetType; }
        public void setAssetType(AssetType assetType) { this.assetType = assetType; }
        public UUID getCategoryId() { return categoryId; }
        public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }
        public String getPurchasePrice() { return purchasePrice; }
        public void setPurchasePrice(String purchasePrice) { this.purchasePrice = purchasePrice; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public LocalDate getPurchaseDate() { return purchaseDate; }
        public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
        public String getSupplier() { return supplier; }
        public void setSupplier(String supplier) { this.supplier = supplier; }
        public String getInvoiceNumber() { return invoiceNumber; }
        public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
        public String getPurchaseOrderNumber() { return purchaseOrderNumber; }
        public void setPurchaseOrderNumber(String purchaseOrderNumber) { this.purchaseOrderNumber = purchaseOrderNumber; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
        public String getResponsiblePerson() { return responsiblePerson; }
        public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }
        public Integer getUsefulLifeYears() { return usefulLifeYears; }
        public void setUsefulLifeYears(Integer usefulLifeYears) { this.usefulLifeYears = usefulLifeYears; }
        public String getDepreciationMethod() { return depreciationMethod; }
        public void setDepreciationMethod(String depreciationMethod) { this.depreciationMethod = depreciationMethod; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class DisposeAssetRequest {
        private LocalDate disposalDate;
        private String reason;

        public LocalDate getDisposalDate() { return disposalDate; }
        public void setDisposalDate(LocalDate disposalDate) { this.disposalDate = disposalDate; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class CreateAssetResponse {
        private final String assetId;

        public CreateAssetResponse(AssetId assetId) {
            this.assetId = assetId.toString();
        }

        public String getAssetId() { return assetId; }
    }
}