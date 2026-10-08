package tech.kayys.syirkah.crm.application.territory;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.crm.application.api.command.RetireTerritoryCommand;
import tech.kayys.syirkah.crm.domain.event.territory.TerritoryRetired;
import tech.kayys.syirkah.crm.domain.territory.Territory;
import tech.kayys.syirkah.crm.domain.repository.TerritoryRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;

import java.util.Objects;
import java.util.Optional;

/**
 * Handles retiring a territory.
 */
public final class RetireTerritoryHandler
        implements CommandHandler<RetireTerritoryCommand, Result<Territory>> {

    private final TerritoryRepository territoryRepository;
    private final EventPublisher eventPublisher;
    private final UnitOfWork unitOfWork;

    public RetireTerritoryHandler(TerritoryRepository territoryRepository,
                                  EventPublisher eventPublisher,
                                  UnitOfWork unitOfWork) {
        this.territoryRepository = Objects.requireNonNull(territoryRepository, "territoryRepository cannot be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher cannot be null");
        this.unitOfWork = Objects.requireNonNull(unitOfWork, "unitOfWork cannot be null");
    }

    @Override
    public Uni<Result<Territory>> handle(RetireTerritoryCommand command) {
        return unitOfWork.execute(() -> Uni.createFrom().completionStage(
                        territoryRepository.findById(command.territoryId()))
                .flatMap(optTerritory -> {
                    Territory territory = optTerritory.orElse(null);
                    if (territory == null) {
                        return Uni.createFrom().item(Result.failure(
                                ApplicationError.of("TERRITORY_NOT_FOUND", "Territory not found with id: " + command.territoryId())));
                    }
                    if (!territory.isActive()) {
                        return Uni.createFrom().item(Result.success(territory));
                    }
                    territory.retire();
                    return Uni.createFrom().completionStage(territoryRepository.save(territory))
                            .flatMap(saved -> eventPublisher.publish(saved.pullDomainEvents())
                                    .replaceWith(Result.success(saved)));
                }));
    }
}