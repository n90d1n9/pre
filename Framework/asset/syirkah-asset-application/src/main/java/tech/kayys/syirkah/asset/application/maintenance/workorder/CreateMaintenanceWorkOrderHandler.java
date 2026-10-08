package tech.kayys.syirkah.asset.application.maintenance.workorder;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.asset.domain.maintenance.AssetReference;
import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrder;
import tech.kayys.syirkah.asset.domain.maintenance.workorder.MaintenanceWorkOrderId;
import tech.kayys.syirkah.asset.domain.repository.MaintenanceWorkOrderRepository;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.foundation.application.transaction.UnitOfWork;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.util.Objects;
import java.util.UUID;

public class CreateMaintenanceWorkOrderHandler extends AbstractMaintenanceCommandHandler
        implements CommandHandler<CreateMaintenanceWorkOrderCommand, Result<MaintenanceWorkOrderResult>> {

    private final AssetReference assetReference;

    public CreateMaintenanceWorkOrderHandler(MaintenanceWorkOrderRepository repository,
                                             AssetReference assetReference,
                                             EventPublisher eventPublisher,
                                             UnitOfWork unitOfWork,
                                             DomainClock clock) {
        super(repository, eventPublisher, unitOfWork, clock);
        this.assetReference = Objects.requireNonNull(assetReference, "assetReference");
    }

    @Override
    public Uni<Result<MaintenanceWorkOrderResult>> handle(CreateMaintenanceWorkOrderCommand command) {
        return requireAsset(assetReference, command.tenantId(), command.assetId())
                .flatMap(ignored -> Uni.createFrom().completionStage(() -> {
                    String number = (command.workOrderNumber() == null || command.workOrderNumber().isBlank())
                            ? "WO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
                            : command.workOrderNumber().trim();
                    return repository.existsByNumber(command.tenantId(), number)
                            .thenApply(exists -> number);
                }))
                .flatMap(number -> Uni.createFrom().completionStage(()
                        -> repository.existsByNumber(command.tenantId(), number))
                        .flatMap(exists -> {
                            if (Boolean.TRUE.equals(exists)) {
                                return Uni.createFrom().item(Result.<MaintenanceWorkOrderResult>failure(
                                        ApplicationError.of("maintenance.work-order.duplicate",
                                                "Work order number already exists: " + number)));
                            }
                            MaintenanceWorkOrder wo = MaintenanceWorkOrder.create(
                                    MaintenanceWorkOrderId.generate(), command.tenantId(),
                                    command.assetId(), number, command.title(),
                                    command.description(), command.type(), command.priority(),
                                    command.requestedBy(), clock);
                            return save(command.tenantId(), wo)
                                    .map(saved -> Result.success(MaintenanceWorkOrderResult.from(saved)));
                        }));
    }
}
