package tech.kayys.erp.crm.application.api.query;

import tech.kayys.erp.crm.domain.model.SupportTicket;
import tech.kayys.erp.crm.domain.valueobject.SLAStatus;
import tech.kayys.erp.crm.domain.valueobject.TicketEscalationLevel;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Complete view of a support ticket.
 */
public record TicketView(
        String ticketId,
        String ticketNumber,
        String customerId,
        String customerName,
        String subject,
        String description,
        String status,
        String statusDescription,
        String priority,
        String priorityDescription,
        String category,
        String subCategory,
        String assignedTo,
        String assignedAt,
        String slaStatus,
        String escalationLevel,
        int escalationCount,
        String firstResponseAt,
        String lastResponseAt,
        int responseCount,
        int internalResponseCount,
        String timeToFirstResponse,
        String timeToResolution,
        String resolution,
        String createdAt,
        String resolvedAt,
        String closedAt,
        String satisfactionRating,
        String satisfactionComment,
        List<TicketCommentView> comments,
        boolean active,
        boolean overdue
) {

    public static TicketView fromDomain(SupportTicket ticket) {
        return new TicketView(
            ticket.getId().toString(),
            ticket.getTicketNumber(),
            ticket.getCustomerId().toString(),
            ticket.getCustomerName(),
            ticket.getSubject(),
            ticket.getDescription(),
            ticket.getStatus().name(),
            ticket.getStatus().getDescription(),
            ticket.getPriority().name(),
            ticket.getPriority().getDescription(),
            ticket.getCategory(),
            ticket.getSubCategory(),
            ticket.getAssignedTo(),
            ticket.getAssignedAt() != null ? ticket.getAssignedAt().toString() : null,
            ticket.getSlaStatus() != null ? ticket.getSlaStatus().name() : null,
            ticket.getEscalationLevel() != null ? ticket.getEscalationLevel().name() : null,
            ticket.getEscalationCount(),
            ticket.getFirstResponseAt() != null ? ticket.getFirstResponseAt().toString() : null,
            ticket.getLastResponseAt() != null ? ticket.getLastResponseAt().toString() : null,
            ticket.getResponseCount(),
            ticket.getInternalResponseCount(),
            ticket.getTimeToFirstResponse(),
            ticket.getTimeToResolution(),
            ticket.getResolution(),
            ticket.getCreatedAt().toString(),
            ticket.getResolvedAt() != null ? ticket.getResolvedAt().toString() : null,
            ticket.getClosedAt() != null ? ticket.getClosedAt().toString() : null,
            ticket.getSatisfactionRating(),
            ticket.getSatisfactionComment(),
            ticket.getComments().stream()
                .map(TicketCommentView::fromDomain)
                .collect(Collectors.toList()),
            ticket.isActive(),
            ticket.isOverdue()
        );
    }

    public record TicketCommentView(
            String id,
            String author,
            String content,
            boolean internal,
            String createdAt
    ) {
        public static TicketCommentView fromDomain(SupportTicket.TicketComment comment) {
            return new TicketCommentView(
                comment.getId(),
                comment.getAuthor(),
                comment.getContent(),
                comment.isInternal(),
                comment.getCreatedAt().toString()
            );
        }
    }
}
// Add these endpoints to the existing TicketResource class:

@POST
@Path("/{id}/comments")
@Operation(summary = "Add a comment to a ticket")
@APIResponse(responseCode = "200", description = "Comment added")
@APIResponse(responseCode = "404", description = "Ticket not found")
public CompletionStage<Response> addComment(
        @PathParam("id") UUID id,
        @Valid AddCommentRequest request) {
    TicketId ticketId = TicketId.of(id);

    AddTicketCommentCommand command = AddTicketCommentCommand.builder()
        .ticketId(ticketId)
        .author(request.getAuthor())
        .content(request.getContent())
        .internal(request.isInternal())
        .build();

    return crmService.addTicketComment(command)
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
@Path("/{id}/escalate")
@Operation(summary = "Escalate a ticket")
@APIResponse(responseCode = "200", description = "Ticket escalated")
@APIResponse(responseCode = "400", description = "Invalid escalation")
public CompletionStage<Response> escalateTicket(
        @PathParam("id") UUID id,
        @Valid EscalateTicketRequest request) {
    TicketId ticketId = TicketId.of(id);

    EscalateTicketCommand command = EscalateTicketCommand.builder()
        .ticketId(ticketId)
        .escalatedTo(request.getEscalatedTo())
        .reason(request.getReason())
        .build();

    return crmService.escalateTicket(command)
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
@Path("/{id}/satisfaction")
@Operation(summary = "Record satisfaction rating")
@APIResponse(responseCode = "200", description = "Satisfaction recorded")
@APIResponse(responseCode = "404", description = "Ticket not found")
public CompletionStage<Response> recordSatisfaction(
        @PathParam("id") UUID id,
        @Valid SatisfactionRequest request) {
    TicketId ticketId = TicketId.of(id);

    RecordSatisfactionCommand command = RecordSatisfactionCommand.builder()
        .ticketId(ticketId)
        .rating(request.getRating())
        .comment(request.getComment())
        .build();

    return crmService.recordSatisfaction(command)
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
@Path("/sla-breached")
@Operation(summary = "Get SLA breached tickets")
@APIResponse(responseCode = "200", description = "List of breached tickets")
public CompletionStage<Response> getSlaBreachedTickets() {
    return crmService.getSlaBreachedTickets()
        .thenApply(Response::ok)
        .thenApply(Response.ResponseBuilder::build);
}

// Additional DTOs
public static class AddCommentRequest {
    private String author;
    private String content;
    private boolean internal;

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }
}

public static class EscalateTicketRequest {
    private String escalatedTo;
    private String reason;

    public String getEscalatedTo() { return escalatedTo; }
    public void setEscalatedTo(String escalatedTo) { this.escalatedTo = escalatedTo; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

public static class SatisfactionRequest {
    private String rating;
    private String comment;

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}