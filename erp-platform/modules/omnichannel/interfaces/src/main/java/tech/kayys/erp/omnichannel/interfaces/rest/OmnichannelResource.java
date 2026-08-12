package tech.kayys.erp.omnichannel.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.omnichannel.application.api.OmnichannelService;
import tech.kayys.erp.omnichannel.application.api.command.*;
import tech.kayys.erp.omnichannel.domain.identifier.ChannelId;
import tech.kayys.erp.omnichannel.domain.identifier.OmniOrderId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for omnichannel operations.
 */
@Path("/api/v1/omnichannel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Omnichannel API", description = "Unified commerce operations")
public class OmnichannelResource {

    @Inject
    OmnichannelService omnichannelService;

    // ============ Channel Endpoints ============

    @POST
    @Path("/channels")
    @Operation(summary = "Register a new channel")
    public CompletionStage<Response> registerChannel(@Valid RegisterChannelRequest request) {
        RegisterChannelCommand command = RegisterChannelCommand.builder()
            .name(request.getName())
            .code(request.getCode())
            .channelType(request.getChannelType())
            .currencyCode(request.getCurrencyCode())
            .storeId(request.getStoreId())
            .region(request.getRegion())
            .fulfillmentMethods(request.getFulfillmentMethods())
            .build();

        return omnichannelService.registerChannel(command)
            .thenApply(channelId -> Response
                .created(URI.create("/api/v1/omnichannel/channels/" + channelId.getValue()))
                .entity(new RegisterChannelResponse(channelId))
                .build()
            );
    }

    @GET
    @Path("/channels")
    @Operation(summary = "Get all channels")
    public CompletionStage<Response> getAllChannels() {
        return omnichannelService.getAllChannels()
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/channels/{id}")
    @Operation(summary = "Get channel details")
    public CompletionStage<Response> getChannel(@PathParam("id") UUID id) {
        ChannelId channelId = ChannelId.of(id);
        return omnichannelService.getChannel(channelId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Order Endpoints ============

    @POST
    @Path("/orders")
    @Operation(summary = "Create an omnichannel order")
    public CompletionStage<Response> createOmniOrder(@Valid CreateOmniOrderRequest request) {
        CreateOmniOrderCommand command = new CreateOmniOrderCommand(
            request.getSalesOrderId(),
            request.getChannelId(),
            request.getCustomerId(),
            request.getFulfillmentMethod(),
            request.getShippingAddress(),
            request.getBillingAddress(),
            request.getCurrencyCode()
        );

        return omnichannelService.createOmniOrder(command)
            .thenApply(orderId -> Response
                .created(URI.create("/api/v1/omnichannel/orders/" + orderId.getValue()))
                .entity(new CreateOmniOrderResponse(orderId))
                .build()
            );
    }

    @GET
    @Path("/orders/{id}")
    @Operation(summary = "Get omnichannel order")
    public CompletionStage<Response> getOmniOrder(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        return omnichannelService.getOmniOrder(orderId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    @Path("/orders/{id}/allocate")
    @Operation(summary = "Allocate inventory for order")
    public CompletionStage<Response> allocateInventory(
            @PathParam("id") UUID id,
            @Valid AllocateInventoryRequest request) {
        OmniOrderId orderId = OmniOrderId.of(id);
        AllocateInventoryCommand command = new AllocateInventoryCommand(
            orderId,
            request.getFulfillmentStoreId(),
            request.getAllocationItems()
        );
        return omnichannelService.allocateInventory(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/pickup-ready")
    @Operation(summary = "Mark order as ready for pickup")
    public CompletionStage<Response> readyForPickup(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        ReadyForPickupCommand command = new ReadyForPickupCommand(orderId);
        return omnichannelService.readyForPickup(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/pickup-complete")
    @Operation(summary = "Complete pickup")
    public CompletionStage<Response> completePickup(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        CompletePickupCommand command = new CompletePickupCommand(orderId);
        return omnichannelService.completePickup(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/ship")
    @Operation(summary = "Ship order")
    public CompletionStage<Response> shipOrder(
            @PathParam("id") UUID id,
            @Valid ShipOrderRequest request) {
        OmniOrderId orderId = OmniOrderId.of(id);
        ShipOrderCommand command = new ShipOrderCommand(
            orderId,
            request.getTrackingNumber(),
            request.getCarrier()
        );
        return omnichannelService.shipOrder(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/deliver")
    @Operation(summary = "Deliver order")
    public CompletionStage<Response> deliverOrder(@PathParam("id") UUID id) {
        OmniOrderId orderId = OmniOrderId.of(id);
        DeliverOrderCommand command = new DeliverOrderCommand(orderId);
        return omnichannelService.deliverOrder(command)
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/orders/{id}/cancel")
    @Operation(summary = "Cancel order")
    public CompletionStage<Response> cancelOrder(
            @PathParam("id") UUID id,
            @Valid CancelOrderRequest request) {
        OmniOrderId orderId = OmniOrderId.of(id);
        CancelOmniOrderCommand command = new CancelOmniOrderCommand(
            orderId,
            request.getReason()
        );
        return omnichannelService.cancelOmniOrder(command)
            .thenApply(response -> Response.ok().build());
    }

    @GET
    @Path("/orders/search")
    @Operation(summary = "Search omnichannel orders")
    public CompletionStage<Response> searchOrders(
            @QueryParam("customerId") String customerId,
            @QueryParam("channelId") UUID channelId,
            @QueryParam("status") String status,
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        SearchOmniOrdersQuery query = new SearchOmniOrdersQuery(
            customerId,
            channelId != null ? ChannelId.of(channelId) : null,
            status != null ? OrderStatus.valueOf(status) : null,
            fromDate != null ? Instant.parse(fromDate) : null,
            toDate != null ? Instant.parse(toDate) : null,
            page,
            size
        );
        return omnichannelService.searchOmniOrders(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Fulfillment Recommendation ============

    @POST
    @Path("/fulfillment/recommend")
    @Operation(summary = "Get fulfillment recommendation")
    public CompletionStage<Response> findFulfillmentLocation(
            @Valid FindFulfillmentLocationRequest request) {
        FindFulfillmentLocationCommand command = new FindFulfillmentLocationCommand(
            request.getProductIds(),
            request.getQuantities(),
            request.getCustomerLocation(),
            request.getChannelId()
        );
        return omnichannelService.findFulfillmentLocation(command)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Inventory Visibility ============

    @GET
    @Path("/inventory/visibility")
    @Operation(summary = "Get inventory visibility")
    public CompletionStage<Response> getInventoryVisibility(
            @QueryParam("productId") UUID productId,
            @QueryParam("locationId") String locationId,
            @QueryParam("channelId") UUID channelId) {
        InventoryVisibilityQuery query = new InventoryVisibilityQuery(
            productId != null ? productId.toString() : null,
            locationId,
            channelId != null ? ChannelId.of(channelId) : null
        );
        return omnichannelService.getInventoryVisibility(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Analytics ============

    @GET
    @Path("/analytics/channels")
    @Operation(summary = "Get channel analytics")
    public CompletionStage<Response> getChannelAnalytics(
            @QueryParam("fromDate") String fromDate,
            @QueryParam("toDate") String toDate) {
        ChannelAnalyticsQuery query = new ChannelAnalyticsQuery(
            fromDate != null ? Instant.parse(fromDate) : null,
            toDate != null ? Instant.parse(toDate) : null
        );
        return omnichannelService.getChannelAnalytics(query)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/analytics/customer-journey/{customerId}")
    @Operation(summary = "Get customer journey")
    public CompletionStage<Response> getCustomerJourney(@PathParam("customerId") String customerId) {
        return omnichannelService.getCustomerJourney(customerId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    // ============ Request/Response DTOs ============

    public static class RegisterChannelRequest {
        private String name;
        private String code;
        private ChannelType channelType;
        private String currencyCode;
        private String storeId;
        private String region;
        private List<FulfillmentMethod> fulfillmentMethods;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public ChannelType getChannelType() { return channelType; }
        public void setChannelType(ChannelType channelType) { this.channelType = channelType; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
        public String getStoreId() { return storeId; }
        public void setStoreId(String storeId) { this.storeId = storeId; }
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        public List<FulfillmentMethod> getFulfillmentMethods() { return fulfillmentMethods; }
        public void setFulfillmentMethods(List<FulfillmentMethod> fulfillmentMethods) { this.fulfillmentMethods = fulfillmentMethods; }
    }

    public static class RegisterChannelResponse {
        private final ChannelId channelId;

        public RegisterChannelResponse(ChannelId channelId) {
            this.channelId = channelId;
        }

        public UUID getChannelId() { return channelId.getValue(); }
    }

    public static class CreateOmniOrderRequest {
        private UUID salesOrderId;
        private UUID channelId;
        private String customerId;
        private FulfillmentMethod fulfillmentMethod;
        private String shippingAddress;
        private String billingAddress;
        private String currencyCode;

        // Getters and setters
        public UUID getSalesOrderId() { return salesOrderId; }
        public void setSalesOrderId(UUID salesOrderId) { this.salesOrderId = salesOrderId; }
        public UUID getChannelId() { return channelId; }
        public void setChannelId(UUID channelId) { this.channelId = channelId; }
        public String getCustomerId() { return customerId; }
        public void setCustomerId(String customerId) { this.customerId = customerId; }
        public FulfillmentMethod getFulfillmentMethod() { return fulfillmentMethod; }
        public void setFulfillmentMethod(FulfillmentMethod fulfillmentMethod) { this.fulfillmentMethod = fulfillmentMethod; }
        public String getShippingAddress() { return shippingAddress; }
        public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
        public String getBillingAddress() { return billingAddress; }
        public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
        public String getCurrencyCode() { return currencyCode; }
        public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    }

    public static class CreateOmniOrderResponse {
        private final OmniOrderId orderId;

        public CreateOmniOrderResponse(OmniOrderId orderId) {
            this.orderId = orderId;
        }

        public UUID getOrderId() { return orderId.getValue(); }
    }

    public static class AllocateInventoryRequest {
        private String fulfillmentStoreId;
        private List<AllocationItem> allocationItems;

        public String getFulfillmentStoreId() { return fulfillmentStoreId; }
        public void setFulfillmentStoreId(String fulfillmentStoreId) { this.fulfillmentStoreId = fulfillmentStoreId; }
        public List<AllocationItem> getAllocationItems() { return allocationItems; }
        public void setAllocationItems(List<AllocationItem> allocationItems) { this.allocationItems = allocationItems; }
    }

    public static class AllocationItem {
        private String productId;
        private int quantity;

        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class ShipOrderRequest {
        private String trackingNumber;
        private String carrier;

        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
        public String getCarrier() { return carrier; }
        public void setCarrier(String carrier) { this.carrier = carrier; }
    }

    public static class CancelOrderRequest {
        private String reason;

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class FindFulfillmentLocationRequest {
        private List<String> productIds;
        private List<Integer> quantities;
        private String customerLocation;
        private UUID channelId;

        public List<String> getProductIds() { return productIds; }
        public void setProductIds(List<String> productIds) { this.productIds = productIds; }
        public List<Integer> getQuantities() { return quantities; }
        public void setQuantities(List<Integer> quantities) { this.quantities = quantities; }
        public String getCustomerLocation() { return customerLocation; }
        public void setCustomerLocation(String customerLocation) { this.customerLocation = customerLocation; }
        public UUID getChannelId() { return channelId; }
        public void setChannelId(UUID channelId) { this.channelId = channelId; }
    }
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/inventory/domain</module>
    <module>modules/inventory/application</module>
    <module>modules/inventory/infrastructure</module>
    <module>modules/inventory/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/customer/domain</module>
    <module>modules/customer/application</module>
    <module>modules/customer/infrastructure</module>
    <module>modules/customer/interfaces</module>

    <module>modules/grocery-pos/domain</module>
    <module>modules/grocery-pos/application</module>
    <module>modules/grocery-pos/infrastructure</module>
    <module>modules/grocery-pos/interfaces</module>

    <module>modules/kiosk/domain</module>
    <module>modules/kiosk/application</module>
    <module>modules/kiosk/infrastructure</module>
    <module>modules/kiosk/interfaces</module>

    <module>modules/omnichannel/domain</module>
    <module>modules/omnichannel/application</module>
    <module>modules/omnichannel/infrastructure</module>
    <module>modules/omnichannel/interfaces</module>
</modules>