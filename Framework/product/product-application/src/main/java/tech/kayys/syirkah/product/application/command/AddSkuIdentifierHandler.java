package tech.kayys.syirkah.product.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.product.domain.sku.Sku;
import tech.kayys.syirkah.product.domain.sku.SkuId;
import tech.kayys.syirkah.product.spi.port.SkuRepository;

import java.util.Objects;
import java.util.Optional;

/**
 * Adds an external identifier (barcode, EAN, ...) to a SKU.
 * Duplicates are rejected by the aggregate.
 */
public final class AddSkuIdentifierHandler
        implements CommandHandler<AddSkuIdentifierCommand, Result<SkuId>> {

    private static final ApplicationError NOT_FOUND =
            ApplicationError.of(
                    "SKU_NOT_FOUND",
                    "SKU does not exist"
            );

    private final SkuRepository skus;
    private final EventPublisher eventPublisher;

    public AddSkuIdentifierHandler(
            SkuRepository skus,
            EventPublisher eventPublisher
    ) {
        this.skus = Objects.requireNonNull(skus);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<SkuId>> handle(AddSkuIdentifierCommand command) {
        return Uni.createFrom()
                .completionStage(skus.findById(command.skuId()))
                .onItem()
                .transformToUni(maybeSku -> add(maybeSku, command));
    }

    private Uni<Result<SkuId>> add(
            Optional<Sku> maybeSku,
            AddSkuIdentifierCommand command
    ) {
        if (maybeSku.isEmpty()) {
            return Uni.createFrom().item(Result.failure(NOT_FOUND));
        }

        var sku = maybeSku.get();
        sku.addIdentifier(command.identifier());

        return Uni.createFrom()
                .completionStage(skus.save(sku))
                .onItem()
                .transformToUni(saved -> eventPublisher
                        .publish(saved.pullDomainEvents())
                        .replaceWith(Result.success(saved.id())));
    }
}