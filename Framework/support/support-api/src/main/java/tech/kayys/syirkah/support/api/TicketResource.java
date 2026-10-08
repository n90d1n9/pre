package tech.kayys.syirkah.support.api;

import tech.kayys.syirkah.support.application.api.command.CreateTicketCommand;
import tech.kayys.syirkah.support.application.api.handler.CreateTicketHandler;
import tech.kayys.syirkah.support.domain.ticket.TicketId;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

import io.smallrye.mutiny.Uni;

@Path("/api/v1/support/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TicketResource {

    private final CreateTicketHandler createTicketHandler;

    @Inject
    public TicketResource(CreateTicketHandler createTicketHandler) {
        this.createTicketHandler = createTicketHandler;
    }

    @POST
    public Uni<Response> createTicket(CreateTicketCommand command) {
        return createTicketHandler.handle(command)
                .onItem().transform(ticketId -> Response
                        .created(URI.create("/api/v1/support/tickets/" + ticketId.value()))
                        .entity(new CreateTicketResponse(ticketId))
                        .build());
    }

    public record CreateTicketResponse(String ticketId) {
        public CreateTicketResponse(TicketId ticketId) {
            this(ticketId.value().toString());
        }
    }
}
