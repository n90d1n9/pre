package tech.kayys.erp.crm.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.crm.application.api.CrmService;
import tech.kayys.erp.crm.application.api.command.AssignTicketCommand;
import tech.kayys.erp.crm.application.api.command.CloseTicketCommand;
import tech.kayys.erp.crm.application.api.command.CreateTicketCommand;
import tech.kayys.erp.crm.application.api.command.ResolveTicketCommand;
import tech.kayys.erp.crm.application.api.query.GetTicketQuery;
import tech.kayys.erp.crm.application.api.query.TicketView;
import tech.kayys.erp.crm.domain.identifier.TicketId;
import tech.kayys.erp.crm.domain.valueobject.TicketPriority;
import tech.kayys.erp.crm.domain.valueobject.TicketStatus;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for support ticket management.
 */
@Path("/api/v1/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Ticket API", description = "Support ticket management endpoints")
public class TicketResource {

    @Inject
    CrmService crmService;

    @POST
    @Operation(summary = "Create a support ticket")
    @APIResponse(responseCode = "201", description = "Ticket created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createTicket(@Valid CreateTicketRequest request) {
        CreateTicketCommand command = CreateTicketCommand.builder()
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .subject(request.getSubject())
            .description(request.getDescription())
            .priority(request.getPriority() != null ? request.getPriority() : TicketPriority.MEDIUM)
            .category(request.getCategory())
            .build();

        return crmService.createTicket(command)
            .thenApply(ticketId -> Response
                .created(URI.create("/api/v1/tickets/" + ticketId.getValue()))
                .entity(new CreateTicketResponse(ticketId))
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
    @Operation(summary = "Get ticket by ID")
    @APIResponse(responseCode = "200", description = "Ticket found")
    @APIResponse(responseCode = "404", description = "Ticket not found")
    public CompletionStage<Response> getTicket(@PathParam("id") UUID id) {
        TicketId ticketId = TicketId.of(id);
        GetTicketQuery query = new GetTicketQuery(ticketId);

        return crmService.getTicket(query)
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
    @Path("/{id}/assign")
    @Operation(summary = "Assign ticket to agent")
    @APIResponse(responseCode = "200", description = "Ticket assigned")
    @APIResponse(responseCode = "400", description = "Invalid assignment")
    public CompletionStage<Response> assignTicket(
            @PathParam("id") UUID id,
            @Valid AssignTicketRequest request) {
        TicketId ticketId = TicketId.of(id);

        AssignTicketCommand command = AssignTicketCommand.builder()
            .ticketId(ticketId)
            .assignedTo(request.getAssignedTo())
            .build();

        return crmService.assignTicket(command)
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
    @Path("/{id}/resolve")
    @Operation(summary = "Resolve a ticket")
    @APIResponse(responseCode = "200", description = "Ticket resolved")
    @APIResponse(responseCode = "400", description = "Invalid resolution")
    public CompletionStage<Response> resolveTicket(
            @PathParam("id") UUID id,
            @Valid ResolveTicketRequest request) {
        TicketId ticketId = TicketId.of(id);

        ResolveTicketCommand command = ResolveTicketCommand.builder()
            .ticketId(ticketId)
            .resolution(request.getResolution())
            .build();

        return crmService.resolveTicket(command)
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
    @Path("/{id}/close")
    @Operation(summary = "Close a ticket")
    @APIResponse(responseCode = "200", description = "Ticket closed")
    @APIResponse(responseCode = "400", description = "Invalid close")
    public CompletionStage<Response> closeTicket(
            @PathParam("id") UUID id,
            @Valid CloseTicketRequest request) {
        TicketId ticketId = TicketId.of(id);

        CloseTicketCommand command = CloseTicketCommand.builder()
            .ticketId(ticketId)
            .closedBy(request.getClosedBy())
            .build();

        return crmService.closeTicket(command)
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

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class CreateTicketRequest {
        private UUID customerId;
        private String customerName;
        private String subject;
        private String description;
        private TicketPriority priority;
        private String category;

        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public TicketPriority getPriority() { return priority; }
        public void setPriority(TicketPriority priority) { this.priority = priority; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class AssignTicketRequest {
        private String assignedTo;

        public String getAssignedTo() { return assignedTo; }
        public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    }

    public static class ResolveTicketRequest {
        private String resolution;

        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
    }

    public static class CloseTicketRequest {
        private String closedBy;

        public String getClosedBy() { return closedBy; }
        public void setClosedBy(String closedBy) { this.closedBy = closedBy; }
    }

    public static class CreateTicketResponse {
        private final String ticketId;

        public CreateTicketResponse(TicketId ticketId) {
            this.ticketId = ticketId.toString();
        }

        public String getTicketId() { return ticketId; }
    }
}