package tech.kayys.syirkah.identity.adapter.bootstrap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;
import tech.kayys.syirkah.identity.application.command.ChangeUserEmailCommandHandler;
import tech.kayys.syirkah.identity.application.command.DeactivateUserCommandHandler;
import tech.kayys.syirkah.identity.application.command.RegisterUserCommandHandler;
import tech.kayys.syirkah.identity.application.port.PasswordHasher;
import tech.kayys.syirkah.identity.application.port.UserRepository;
import tech.kayys.syirkah.identity.application.query.GetUserByIdQueryHandler;
import tech.kayys.syirkah.identity.application.query.ListUsersQueryHandler;

/**
 * Composition root for the Identity service. Handlers are plain
 * classes with constructor-injected ports (no CDI annotations on the
 * application layer itself, so it stays framework-free) - this is
 * the one place that wires ports (implemented in this module) into
 * use cases (defined in syirkah-identity-application).
 */
@ApplicationScoped
public class IdentityHandlerProducers {

    @Inject
    UserRepository userRepository;

    @Inject
    PasswordHasher passwordHasher;

    @Inject
    EventPublisher eventPublisher;

    @Inject
    UnitOfWork unitOfWork;

    @Inject
    DomainClock domainClock;

    @Produces
    @ApplicationScoped
    public RegisterUserCommandHandler registerUserCommandHandler() {
        return new RegisterUserCommandHandler(
                userRepository, passwordHasher, eventPublisher, unitOfWork, domainClock
        );
    }

    @Produces
    @ApplicationScoped
    public ChangeUserEmailCommandHandler changeUserEmailCommandHandler() {
        return new ChangeUserEmailCommandHandler(
                userRepository, eventPublisher, unitOfWork, domainClock
        );
    }

    @Produces
    @ApplicationScoped
    public DeactivateUserCommandHandler deactivateUserCommandHandler() {
        return new DeactivateUserCommandHandler(
                userRepository, eventPublisher, unitOfWork, domainClock
        );
    }

    @Produces
    @ApplicationScoped
    public GetUserByIdQueryHandler getUserByIdQueryHandler() {
        return new GetUserByIdQueryHandler(userRepository);
    }

    @Produces
    @ApplicationScoped
    public ListUsersQueryHandler listUsersQueryHandler() {
        return new ListUsersQueryHandler(userRepository);
    }

}
