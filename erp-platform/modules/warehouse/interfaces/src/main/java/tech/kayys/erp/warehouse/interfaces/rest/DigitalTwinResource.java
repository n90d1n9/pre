package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseDigitalTwinService;
import tech.kayys.erp.warehouse.application.service.DigitalTwinSimulationService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for warehouse digital twin.
 */
@Path("/api/v1/warehouses/{warehouseId}/digital-twin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Digital Twin API", description = "Warehouse digital twin management endpoints")
public class DigitalTwinResource {

    @Inject
    WarehouseDigitalTwinService digitalTwinService;

    @Inject
    DigitalTwinSimulationService simulationService;

    @GET
    @Path("/status")
    @Operation(summary = "Get digital twin status")
    @APIResponse(responseCode = "200", description = "Digital twin status")
    public CompletionStage<Response> getStatus(@PathParam("warehouseId") UUID warehouseId) {
        return digitalTwinService.getDigitalTwin(warehouseId)
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
    @Path("/layout")
    @Operation(summary = "Get digital twin layout")
    @APIResponse(responseCode = "200", description = "Digital twin layout")
    public CompletionStage<Response> getLayout(@PathParam("warehouseId") UUID warehouseId) {
        return digitalTwinService.getLayout(warehouseId)
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
    @Path("/bins")
    @Operation(summary = "Get virtual bins")
    @APIResponse(responseCode = "200", description = "Virtual bins")
    public CompletionStage<Response> getBins(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("zone") String zone,
            @QueryParam("occupied") Boolean occupied) {
        return digitalTwinService.getBins(warehouseId, zone, occupied)
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
    @Path("/simulate/picking")
    @Operation(summary = "Simulate picking optimization")
    @APIResponse(responseCode = "200", description = "Simulation result")
    public CompletionStage<Response> simulatePicking(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("productIds") List<String> productIds,
            @QueryParam("quantity") int quantity) {
        return digitalTwinService.simulatePicking(warehouseId, productIds, quantity)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
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
    @Path("/simulate/putaway")
    @Operation(summary = "Simulate putaway optimization")
    @APIResponse(responseCode = "200", description = "Simulation result")
    public CompletionStage<Response> simulatePutaway(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("productId") String productId,
            @QueryParam("quantity") int quantity,
            @QueryParam("minVolume") double minVolume) {
        return digitalTwinService.simulatePutaway(warehouseId, productId, quantity, minVolume)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/simulate/layout")
    @Operation(summary = "Simulate layout optimization")
    @APIResponse(responseCode = "200", description = "Simulation result")
    public CompletionStage<Response> simulateLayout(@PathParam("warehouseId") UUID warehouseId) {
        return digitalTwinService.simulateLayout(warehouseId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
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
    @Path("/forecast")
    @Operation(summary = "Simulate inventory forecasting")
    @APIResponse(responseCode = "200", description = "Simulation result")
    public CompletionStage<Response> simulateForecast(
            @PathParam("warehouseId") UUID warehouseId,
            @QueryParam("productId") String productId,
            @QueryParam("days") int days) {
        return digitalTwinService.simulateForecast(warehouseId, productId, days)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.BAD_REQUEST)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @POST
    @Path("/sync")
    @Operation(summary = "Sync digital twin with physical warehouse")
    @APIResponse(responseCode = "200", description = "Sync successful")
    public CompletionStage<Response> sync(@PathParam("warehouseId") UUID warehouseId) {
        return digitalTwinService.syncDigitalTwin(warehouseId)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND)
                        .entity(throwable.getCause().getMessage())
                        .build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }

    @GET
    @Path("/heatmap")
    @Operation(summary = "Get warehouse heatmap data")
    @APIResponse(responseCode = "200", description = "Heatmap data")
    public CompletionStage<Response> getHeatmap(@PathParam("warehouseId") UUID warehouseId) {
        return digitalTwinService.getHeatmap(warehouseId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof IllegalArgumentException) {
                    return Response.status(Response.Status.NOT_FOUND).build();
                }
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            });
    }
}