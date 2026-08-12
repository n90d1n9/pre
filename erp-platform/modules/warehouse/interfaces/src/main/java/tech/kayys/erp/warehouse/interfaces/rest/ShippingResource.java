package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseShippingService;
import tech.kayys.erp.warehouse.application.api.command.AssignCarrierCommand;
import tech.kayys.erp.warehouse.application.api.command.CreateShippingTaskCommand;
import tech.kayys.erp.warehouse.application.api.command.ShipItemCommand;
import tech.kayys.erp.warehouse.domain.identifier.ShippingTaskId;
import tech.kayys.erp.warehouse.domain.valueobject.Carrier;
import tech.kayys.erp.warehouse.domain.valueobject.ShippingMethod;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for shipping operations.
 */
@Path("/api/v1/warehouses/{warehouseId}/shipping")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Shipping API", description = "Warehouse shipping management endpoints")
public class ShippingResource {

    @Inject
    WarehouseShippingService shippingService;

    @POST
    @Path("/tasks")
    @Operation(summary = "Create a shipping task")
    @APIResponse(responseCode = "201", description = "Shipping task created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createShippingTask(
            @PathParam("warehouseId") UUID warehouseId,
            @Valid CreateShippingTaskRequest request) {
        CreateShippingTaskCommand command = CreateShippingTaskCommand.builder()
            .warehouseId(warehouseId)
            .orderReference(request.getOrderReference())
            .orderType(request.getOrderType())
            .customerName(request.getCustomerName())
            .shippingAddress(request.getShippingAddress())
            .city(request.getCity())
            .state(request.getState())
            .postalCode(request.getPostalCode())
            .country(request.getCountry())
            .phone(request.getPhone())
            .email(request.getEmail())
            .shippingNotes(request.getShippingNotes())
            .items(request.getItems())
            .notes(request.getNotes())
            .build();

        return shippingService.createShippingTask(command)
            .thenApply(taskId -> Response
                .created(URI.create("/api/v1/warehouses/" + warehouseId + "/shipping/tasks/" + taskId.getValue()))
                .entity(new CreateShippingTaskResponse(taskId))
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
    @Operation(summary = "Get shipping task by ID")
    @APIResponse(responseCode = "200", description = "Task found")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> getShippingTask(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ShippingTaskId id = ShippingTaskId.of(taskId);
        return shippingService.getShippingTask(id)
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
    @Path("/tasks/{taskId}/pack")
    @Operation(summary = "Start packing")
    @APIResponse(responseCode = "200", description = "Packing started")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> startPacking(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ShippingTaskId id = ShippingTaskId.of(taskId);

        return shippingService.startPacking(id)
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
    @Path("/tasks/{taskId}/ready")
    @Operation(summary = "Mark as ready to ship")
    @APIResponse(responseCode = "200", description = "Ready to ship")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> readyToShip(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ShippingTaskId id = ShippingTaskId.of(taskId);

        return shippingService.readyToShip(id)
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
    @Path("/tasks/{taskId}/carrier")
    @Operation(summary = "Assign carrier")
    @APIResponse(responseCode = "200", description = "Carrier assigned")
    @APIResponse(responseCode = "400", description = "Invalid assignment")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> assignCarrier(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId,
            @Valid AssignCarrierRequest request) {
        ShippingTaskId id = ShippingTaskId.of(taskId);

        AssignCarrierCommand command = AssignCarrierCommand.builder()
            .taskId(id)
            .carrier(request.getCarrier())
            .carrierAccount(request.getCarrierAccount())
            .shippingMethod(request.getShippingMethod())
            .build();

        return shippingService.assignCarrier(command)
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
    @Path("/tasks/{taskId}/ship")
    @Operation(summary = "Ship items")
    @APIResponse(responseCode = "200", description = "Items shipped")
    @APIResponse(responseCode = "400", description = "Invalid ship")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> shipItems(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId,
            @Valid ShipItemsRequest request) {
        ShippingTaskId id = ShippingTaskId.of(taskId);

        ShipItemCommand command = ShipItemCommand.builder()
            .taskId(id)
            .itemId(request.getItemId())
            .quantity(request.getQuantity())
            .shippedBy(request.getShippedBy())
            .trackingNumber(request.getTrackingNumber())
            .build();

        return shippingService.shipItem(command)
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
    @Path("/tasks/{taskId}/deliver")
    @Operation(summary = "Mark as delivered")
    @APIResponse(responseCode = "200", description = "Delivered")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> markDelivered(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("taskId") UUID taskId) {
        ShippingTaskId id = ShippingTaskId.of(taskId);

        return shippingService.markDelivered(id)
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
    @Operation(summary = "Get shipping tasks")
    @APIResponse(responseCode = "200", description = "Tasks found")
    public CompletionStage<Response> getTasks(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("status") String status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return shippingService.getShippingTasksByWarehouse(warehouseId, status, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/tracking/{trackingNumber}")
    @Operation(summary = "Get shipping task by tracking number")
    @APIResponse(responseCode = "200", description = "Task found")
    @APIResponse(responseCode = "404", description = "Task not found")
    public CompletionStage<Response> getByTrackingNumber(
            @PathParam("warehouseId") UUID warehouseId,
            @PathParam("trackingNumber") String trackingNumber) {
        return shippingService.getShippingTaskByTrackingNumber(trackingNumber)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateShippingTaskRequest {
        private String orderReference;
        private String orderType;
        private String customerName;
        private String shippingAddress;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String phone;
        private String email;
        private String shippingNotes;
        private List<CreateShippingTaskCommand.ShippingItemCommand> items;
        private String notes;

        public String getOrderReference() { return orderReference; }
        public void setOrderReference(String orderReference) { this.orderReference = orderReference; }
        public String getOrderType() { return orderType; }
        public void setOrderType(String orderType) { this.orderType = orderType; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
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
        public String getShippingNotes() { return shippingNotes; }
        public void setShippingNotes(String shippingNotes) { this.shippingNotes = shippingNotes; }
        public List<CreateShippingTaskCommand.ShippingItemCommand> getItems() { return items; }
        public void setItems(List<CreateShippingTaskCommand.ShippingItemCommand> items) { this.items = items; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class AssignCarrierRequest {
        private Carrier carrier;
        private String carrierAccount;
        private ShippingMethod shippingMethod;

        public Carrier getCarrier() { return carrier; }
        public void setCarrier(Carrier carrier) { this.carrier = carrier; }
        public String getCarrierAccount() { return carrierAccount; }
        public void setCarrierAccount(String carrierAccount) { this.carrierAccount = carrierAccount; }
        public ShippingMethod getShippingMethod() { return shippingMethod; }
        public void setShippingMethod(ShippingMethod shippingMethod) { this.shippingMethod = shippingMethod; }
    }

    public static class ShipItemsRequest {
        private String itemId;
        private int quantity;
        private String shippedBy;
        private String trackingNumber;

        public String getItemId() { return itemId; }
        public void setItemId(String itemId) { this.itemId = itemId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public String getShippedBy() { return shippedBy; }
        public void setShippedBy(String shippedBy) { this.shippedBy = shippedBy; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    }

    public static class CreateShippingTaskResponse {
        private final String taskId;

        public CreateShippingTaskResponse(ShippingTaskId taskId) {
            this.taskId = taskId.toString();
        }

        public String getTaskId() { return taskId; }
    }
}