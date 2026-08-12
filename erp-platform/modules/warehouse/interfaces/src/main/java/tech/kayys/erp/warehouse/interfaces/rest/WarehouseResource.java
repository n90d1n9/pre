package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseService;
import tech.kayys.erp.warehouse.application.api.command.CreateWarehouseCommand;
import tech.kayys.erp.warehouse.domain.identifier.WarehouseId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for warehouse management.
 */
@Path("/api/v1/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Warehouse API", description = "Warehouse management endpoints")
public class WarehouseResource {

    @Inject
    WarehouseService warehouseService;

    @POST
    @Operation(summary = "Create a new warehouse")
    @APIResponse(responseCode = "201", description = "Warehouse created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    @APIResponse(responseCode = "409", description = "Warehouse code already exists")
    public CompletionStage<Response> createWarehouse(@Valid CreateWarehouseRequest request) {
        CreateWarehouseCommand command = CreateWarehouseCommand.builder()
            .code(request.getCode())
            .name(request.getName())
            .description(request.getDescription())
            .address(request.getAddress())
            .city(request.getCity())
            .state(request.getState())
            .postalCode(request.getPostalCode())
            .country(request.getCountry())
            .phone(request.getPhone())
            .email(request.getEmail())
            .managerId(request.getManagerId())
            .capacity(request.getCapacity())
            .zones(request.getZones())
            .notes(request.getNotes())
            .build();

        return warehouseService.createWarehouse(command)
            .thenApply(warehouseId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId.getValue()))
                .entity(new CreateWarehouseResponse(warehouseId))
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
    @Path("/{id}")
    @Operation(summary = "Get warehouse by ID")
    @APIResponse(responseCode = "200", description = "Warehouse found")
    @APIResponse(responseCode = "404", description = "Warehouse not found")
    public CompletionStage<Response> getWarehouse(@PathParam("id") UUID id) {
        WarehouseId warehouseId = WarehouseId.of(id);
        return warehouseService.getWarehouse(warehouseId)
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
    @Path("/default")
    @Operation(summary = "Get default warehouse")
    @APIResponse(responseCode = "200", description = "Default warehouse found")
    @APIResponse(responseCode = "404", description = "No default warehouse")
    public CompletionStage<Response> getDefaultWarehouse() {
        return warehouseService.getDefaultWarehouse()
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
    @Path("/search")
    @Operation(summary = "Search warehouses")
    @APIResponse(responseCode = "200", description = "Search results")
    public CompletionStage<Response> searchWarehouses(
            @QueryParam("name") String name,
            @QueryParam("country") String country,
            @QueryParam("active") Boolean active,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return warehouseService.searchWarehouses(name, country, active, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateWarehouseRequest {
        private String code;
        private String name;
        private String description;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phone;
        private String email;
        private String managerId;
        private Integer capacity;
        private List<String> zones;
        private String notes;

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getManagerId() { return managerId; }
        public void setManagerId(String managerId) { this.managerId = managerId; }
        public Integer getCapacity() { return capacity; }
        public void setCapacity(Integer capacity) { this.capacity = capacity; }
        public List<String> getZones() { return zones; }
        public void setZones(List<String> zones) { this.zones = zones; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class CreateWarehouseResponse {
        private final String warehouseId;

        public CreateWarehouseResponse(WarehouseId warehouseId) {
            this.warehouseId = warehouseId.toString();
        }

        public String getWarehouseId() { return warehouseId; }
    }
}