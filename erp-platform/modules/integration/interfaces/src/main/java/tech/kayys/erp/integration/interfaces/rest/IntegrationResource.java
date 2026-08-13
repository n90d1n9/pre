package tech.kayys.erp.integration.interfaces.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tech.kayys.erp.integration.application.command.CreateIntegrationCommand;
import tech.kayys.erp.integration.application.handler.IntegrationCommandHandler;
import tech.kayys.erp.integration.domain.valueobject.IntegrationType;

import java.util.UUID;

/**
 * REST Resource for Integration operations.
 */
@Path("/api/integrations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IntegrationResource {

    @Inject
    IntegrationCommandHandler commandHandler;

    @POST
    public Response createIntegration(CreateIntegrationCommand command) {
        try {
            UUID id = commandHandler.handle(command).getValue();
            return Response.created(java.net.URI.create("/api/integrations/" + id)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getIntegration(@PathParam("id") UUID id) {
        return Response.ok().entity("{\"id\": \"" + id + "\", \"status\": \"found\"}").build();
    }

    @GET
    public Response listIntegrations() {
        return Response.ok().entity("{\"integrations\": []}").build();
    }

    @PUT
    @Path("/{id}/activate")
    public Response activateIntegration(@PathParam("id") UUID id) {
        return Response.ok().entity("{\"id\": \"" + id + "\", \"status\": \"activated\"}").build();
    }

    @PUT
    @Path("/{id}/deactivate")
    public Response deactivateIntegration(@PathParam("id") UUID id) {
        return Response.ok().entity("{\"id\": \"" + id + "\", \"status\": \"deactivated\"}").build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteIntegration(@PathParam("id") UUID id) {
        return Response.noContent().build();
    }
}
