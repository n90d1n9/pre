package tech.kayys.syirkah.support.application.api.handler;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.domain.exception.BusinessRuleViolation;
import tech.kayys.syirkah.support.application.api.ActorType;
import tech.kayys.syirkah.support.application.api.SupportActorProvider;
import tech.kayys.syirkah.support.application.api.command.CreatePortalTicketCommand;
import tech.kayys.syirkah.support.application.api.command.CreateTicketCommand;
import tech.kayys.syirkah.support.domain.ticket.Requester;
import tech.kayys.syirkah.support.domain.ticket.RequesterRole;
import tech.kayys.syirkah.support.domain.ticket.TicketId;

import java.util.Objects;

public final class CreatePortalTicketHandler
        implements CommandHandler<CreatePortalTicketCommand, TicketId> {

    private final SupportActorProvider actorProvider;
    private final CreateTicketHandler createTicketHandler;

    public CreatePortalTicketHandler(SupportActorProvider actorProvider,
                                     CreateTicketHandler createTicketHandler) {
        this.actorProvider = Objects.requireNonNull(actorProvider);
        this.createTicketHandler = Objects.requireNonNull(createTicketHandler);
    }

    @Override
    public Uni<TicketId> handle(CreatePortalTicketCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        return actorProvider.currentActor().flatMap(actor -> {
            if (actor.type() != ActorType.CUSTOMER || actor.participantId() == null) {
                return Uni.createFrom().failure(
                        new BusinessRuleViolation("Portal ticket requires an authenticated customer party"));
            }
            var create = new CreateTicketCommand(
                    actor.tenantId(),
                    new Requester.Party(actor.participantId(), RequesterRole.CUSTOMER),
                    command.type(),
                    command.priority(),
                    command.subject(),
                    command.description());
            return createTicketHandler.handle(create);
        });
    }
}
