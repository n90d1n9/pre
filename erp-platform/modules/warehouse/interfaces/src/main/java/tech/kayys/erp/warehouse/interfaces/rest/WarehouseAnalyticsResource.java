package tech.kayys.erp.warehouse.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.warehouse.application.api.WarehouseAnalyticsService;
import tech.kayys.erp.warehouse.application.api.query.WarehouseAnalyticsView;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;

/**
 * REST API for warehouse analytics.
 */
@Path("/api/v1/warehouses/analytics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Warehouse Analytics", description = "Warehouse analytics and reporting endpoints")
public class WarehouseAnalyticsResource {

    @Inject
    WarehouseAnalyticsService analyticsService;

    @GET
    @Path("/dashboard")
    @Operation(summary = "Get warehouse dashboard metrics")
    @APIResponse(responseCode = "200", description = "Dashboard metrics")
    public CompletionStage<Response> getDashboard(
            @QueryParam("period") @DefaultValue("LAST_7_DAYS") String period) {
        Instant end = Instant.now();
        Instant start = switch (period) {
            case "TODAY" -> end.minus(1, ChronoUnit.DAYS);
            case "YESTERDAY" -> end.minus(2, ChronoUnit.DAYS);
            case "LAST_7_DAYS" -> end.minus(7, ChronoUnit.DAYS);
            case "LAST_30_DAYS" -> end.minus(30, ChronoUnit.DAYS);
            case "LAST_90_DAYS" -> end.minus(90, ChronoUnit.DAYS);
            default -> end.minus(7, ChronoUnit.DAYS);
        };

        return analyticsService.getDashboardAnalytics(start, end)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/utilization")
    @Operation(summary = "Get warehouse utilization report")
    @APIResponse(responseCode = "200", description = "Utilization report")
    public CompletionStage<Response> getUtilizationReport() {
        return analyticsService.getUtilizationReport()
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/activity")
    @Operation(summary = "Get warehouse activity report")
    @APIResponse(responseCode = "200", description = "Activity report")
    public CompletionStage<Response> getActivityReport(
            @QueryParam("warehouseId") String warehouseId,
            @QueryParam("days") @DefaultValue("7") int days) {
        return analyticsService.getActivityReport(warehouseId, days)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/performance")
    @Operation(summary = "Get warehouse performance metrics")
    @APIResponse(responseCode = "200", description = "Performance metrics")
    public CompletionStage<Response> getPerformanceMetrics(
            @QueryParam("warehouseId") String warehouseId,
            @QueryParam("period") @DefaultValue("LAST_7_DAYS") String period) {
        Instant end = Instant.now();
        Instant start = switch (period) {
            case "TODAY" -> end.minus(1, ChronoUnit.DAYS);
            case "LAST_7_DAYS" -> end.minus(7, ChronoUnit.DAYS);
            case "LAST_30_DAYS" -> end.minus(30, ChronoUnit.DAYS);
            case "LAST_90_DAYS" -> end.minus(90, ChronoUnit.DAYS);
            default -> end.minus(7, ChronoUnit.DAYS);
        };

        return analyticsService.getPerformanceMetrics(warehouseId, start, end)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/inventory-value")
    @Operation(summary = "Get inventory valuation report")
    @APIResponse(responseCode = "200", description = "Inventory valuation")
    public CompletionStage<Response> getInventoryValuation() {
        return analyticsService.getInventoryValuation()
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }
}