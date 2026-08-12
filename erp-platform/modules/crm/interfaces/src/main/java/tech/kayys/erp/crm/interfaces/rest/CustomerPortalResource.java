package tech.kayys.erp.crm.interfaces.rest;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import tech.kayys.erp.crm.application.api.CrmService;
import tech.kayys.erp.crm.application.api.command.CreatePortalTicketCommand;
import tech.kayys.erp.crm.application.api.command.RegisterPortalUserCommand;
import tech.kayys.erp.crm.domain.identifier.TicketId;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

/**
 * REST API for customer portal.
 */
@Path("/api/v1/portal")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Customer Portal", description = "Customer self-service portal endpoints")
public class CustomerPortalResource {

    @Inject
    CrmService crmService;

    @POST
    @Path("/register")
    @Operation(summary = "Register for portal access")
    @APIResponse(responseCode = "200", description = "Registration successful")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> register(@Valid RegisterPortalRequest request) {
        RegisterPortalUserCommand command = RegisterPortalUserCommand.builder()
            .customerId(request.getCustomerId())
            .customerName(request.getCustomerName())
            .email(request.getEmail())
            .username(request.getUsername())
            .password(request.getPassword())
            .build();

        return crmService.registerPortalUser(command)
            .thenApply(userId -> Response
                .ok(new RegisterPortalResponse(userId))
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

    @POST
    @Path("/tickets")
    @Operation(summary = "Create a ticket via portal")
    @APIResponse(responseCode = "201", description = "Ticket created")
    @APIResponse(responseCode = "400", description = "Invalid input")
    public CompletionStage<Response> createTicket(@Valid CreatePortalTicketRequest request) {
        CreatePortalTicketCommand command = CreatePortalTicketCommand.builder()
            .customerId(request.getCustomerId())
            .subject(request.getSubject())
            .description(request.getDescription())
            .priority(request.getPriority())
            .category(request.getCategory())
            .build();

        return crmService.createPortalTicket(command)
            .thenApply(ticketId -> Response
                .created(URI.create("/api/v1/portal/tickets/" + ticketId.getValue()))
                .entity(new CreatePortalTicketResponse(ticketId))
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
    @Path("/tickets/{id}")
    @Operation(summary = "Get ticket via portal")
    @APIResponse(responseCode = "200", description = "Ticket found")
    @APIResponse(responseCode = "404", description = "Ticket not found")
    public CompletionStage<Response> getTicket(@PathParam("id") UUID id) {
        TicketId ticketId = TicketId.of(id);
        return crmService.getPortalTicket(ticketId)
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
    @Path("/tickets")
    @Operation(summary = "Get customer tickets via portal")
    @APIResponse(responseCode = "200", description = "Tickets found")
    public CompletionStage<Response> getCustomerTickets(
            @QueryParam("customerId") UUID customerId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return crmService.getPortalTickets(customerId, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/knowledge")
    @Operation(summary = "Search knowledge base")
    @APIResponse(responseCode = "200", description = "Articles found")
    public CompletionStage<Response> searchKnowledge(
            @QueryParam("q") String query,
            @QueryParam("category") String category,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        return crmService.searchKnowledgeArticles(query, category, page, size)
            .thenApply(Response::ok)
            .thenApply(Response.ResponseBuilder::build);
    }

    @GET
    @Path("/knowledge/{id}")
    @Operation(summary = "Get knowledge article")
    @APIResponse(responseCode = "200", description = "Article found")
    @APIResponse(responseCode = "404", description = "Article not found")
    public CompletionStage<Response> getKnowledgeArticle(@PathParam("id") UUID id) {
        return crmService.getKnowledgeArticle(id)
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
    @Path("/knowledge/{id}/helpful")
    @Operation(summary = "Mark article as helpful")
    @APIResponse(responseCode = "200", description = "Feedback recorded")
    public CompletionStage<Response> markHelpful(@PathParam("id") UUID id) {
        return crmService.markArticleHelpful(id)
            .thenApply(response -> Response.ok().build())
            .exceptionally(throwable -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
    }

    // =========================================================================
    // Request/Response DTOs
    // =========================================================================

    public static class RegisterPortalRequest {
        private UUID customerId;
        private String customerName;
        private String email;
        private String username;
        private String password;

        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class CreatePortalTicketRequest {
        private UUID customerId;
        private String subject;
        private String description;
        private String priority;
        private String category;

        public UUID getCustomerId() { return customerId; }
        public void setCustomerId(UUID customerId) { this.customerId = customerId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class RegisterPortalResponse {
        private final String userId;

        public RegisterPortalResponse(CustomerPortalUserId userId) {
            this.userId = userId.toString();
        }

        public String getUserId() { return userId; }
    }

    public static class CreatePortalTicketResponse {
        private final String ticketId;

        public CreatePortalTicketResponse(TicketId ticketId) {
            this.ticketId = ticketId.toString();
        }

        public String getTicketId() { return ticketId; }
    }
}