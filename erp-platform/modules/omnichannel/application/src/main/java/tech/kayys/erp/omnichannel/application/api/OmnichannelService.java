package tech.kayys.erp.omnichannel.application.api;

import tech.kayys.erp.omnichannel.application.api.command.*;
import tech.kayys.erp.omnichannel.application.api.query.*;
import tech.kayys.erp.omnichannel.domain.identifier.ChannelId;
import tech.kayys.erp.omnichannel.domain.identifier.OmniOrderId;

import java.util.concurrent.CompletionStage;

/**
 * Public API for omnichannel operations.
 */
public interface OmnichannelService {

    // ============ Channel Operations ============

    /**
     * Registers a new channel.
     */
    CompletionStage<ChannelId> registerChannel(RegisterChannelCommand command);

    /**
     * Updates channel settings.
     */
    CompletionStage<ChannelId> updateChannel(UpdateChannelCommand command);

    /**
     * Gets channel details.
     */
    CompletionStage<ChannelView> getChannel(ChannelId channelId);

    /**
     * Gets all channels.
     */
    CompletionStage<List<ChannelView>> getAllChannels();

    // ============ Inventory Visibility ============

    /**
     * Gets inventory visibility across channels.
     */
    CompletionStage<InventoryVisibilityView> getInventoryVisibility(InventoryVisibilityQuery query);

    /**
     * Updates inventory visibility for a product.
     */
    CompletionStage<Void> updateInventoryVisibility(UpdateInventoryVisibilityCommand command);

    // ============ Order Operations ============

    /**
     * Creates an omnichannel order.
     */
    CompletionStage<OmniOrderId> createOmniOrder(CreateOmniOrderCommand command);

    /**
     * Gets order details.
     */
    CompletionStage<OmniOrderView> getOmniOrder(OmniOrderId orderId);

    /**
     * Searches orders across channels.
     */
    CompletionStage<OmniOrderSearchResult> searchOmniOrders(SearchOmniOrdersQuery query);

    /**
     * Updates order status.
     */
    CompletionStage<OmniOrderId> updateOmniOrderStatus(UpdateOmniOrderStatusCommand command);

    /**
     * Cancels an omnichannel order.
     */
    CompletionStage<OmniOrderId> cancelOmniOrder(CancelOmniOrderCommand command);

    // ============ Fulfillment Operations ============

    /**
     * Finds the best fulfillment location for an order.
     */
    CompletionStage<FulfillmentRecommendation> findFulfillmentLocation(
        FindFulfillmentLocationCommand command
    );

    /**
     * Allocates inventory for an order.
     */
    CompletionStage<OmniOrderId> allocateInventory(AllocateInventoryCommand command);

    /**
     * Marks order as ready for pickup.
     */
    CompletionStage<OmniOrderId> readyForPickup(ReadyForPickupCommand command);

    /**
     * Completes pickup.
     */
    CompletionStage<OmniOrderId> completePickup(CompletePickupCommand command);

    /**
     * Ships an order.
     */
    CompletionStage<OmniOrderId> shipOrder(ShipOrderCommand command);

    /**
     * Delivers an order.
     */
    CompletionStage<OmniOrderId> deliverOrder(DeliverOrderCommand command);

    // ============ Analytics ============

    /**
     * Gets channel analytics.
     */
    CompletionStage<ChannelAnalytics> getChannelAnalytics(ChannelAnalyticsQuery query);

    /**
     * Gets cross-channel customer journey.
     */
    CompletionStage<CustomerJourneyView> getCustomerJourney(String customerId);
}