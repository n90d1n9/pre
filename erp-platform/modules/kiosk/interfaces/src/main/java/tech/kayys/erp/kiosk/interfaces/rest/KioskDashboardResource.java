package tech.kayys.erp.kiosk.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.kiosk.application.api.KioskDashboardService;
import tech.kayys.erp.kiosk.domain.identifier.KioskId;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for kiosk dashboard and monitoring.
 */
@Path("/api/v1/kiosks/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Kiosk Dashboard API", description = "Kiosk monitoring and management")
public class KioskDashboardResource {

    @Inject
    KioskDashboardService dashboardService;

    @GET
    @Path("/{id}/status")
    @Operation(summary = "Get kiosk status")
    public CompletionStage<Response> getKioskStatus(@PathParam("id") UUID id) {
        KioskId kioskId = KioskId.of(id);
        return dashboardService.getKioskStatus(kioskId)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Operation(summary = "Get all kiosk statuses")
    public CompletionStage<Response> getAllKioskStatuses() {
        return dashboardService.getAllKioskStatuses()
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/{id}/performance")
    @Operation(summary = "Get kiosk performance metrics")
    public CompletionStage<Response> getKioskPerformance(
            @PathParam("id") UUID id,
            @QueryParam("period") @DefaultValue("WEEK") KioskDashboardService.PerformancePeriod period) {
        KioskId kioskId = KioskId.of(id);
        return dashboardService.getKioskPerformance(kioskId, period)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @POST
    @Path("/{id}/commands")
    @Operation(summary = "Send command to kiosk")
    public CompletionStage<Response> sendCommand(
            @PathParam("id") UUID id,
            SendCommandRequest request) {
        KioskId kioskId = KioskId.of(id);
        return dashboardService.sendKioskCommand(kioskId, request.getCommand())
            .thenApply(response -> Response.ok().build());
    }

    @POST
    @Path("/{id}/alerts")
    @Operation(summary = "Send kiosk alert")
    public CompletionStage<Response> sendAlert(
            @PathParam("id") UUID id,
            SendAlertRequest request) {
        KioskId kioskId = KioskId.of(id);
        return dashboardService.sendKioskAlert(kioskId, request.getAlert())
            .thenApply(response -> Response.ok().build());
    }

    // Request DTOs
    public static class SendCommandRequest {
        private KioskDashboardService.KioskCommand command;

        public KioskDashboardService.KioskCommand getCommand() { return command; }
        public void setCommand(KioskDashboardService.KioskCommand command) { this.command = command; }
    }

    public static class SendAlertRequest {
        private KioskDashboardService.KioskAlert alert;

        public KioskDashboardService.KioskAlert getAlert() { return alert; }
        public void setAlert(KioskDashboardService.KioskAlert alert) { this.alert = alert; }
    }
}