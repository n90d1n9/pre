package tech.kayys.syirkah.product.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.product.domain.sku.Sku;
import tech.kayys.syirkah.product.domain.sku.SkuId;
import tech.kayys.syirkah.product.spi.port.ProductRepository;
import tech.kayys.syirkah.product.spi.port.SkuRepository;

import java.util.Objects;

/**
 * Creates a stock keeping unit, rejecting a duplicate SKU code and
 * requiring the referenced product to exist.
 */
public final class CreateSkuHandler
        implements CommandHandler<CreateSkuCommand, Result<SkuId>> {

    private static final ApplicationError PRODUCT_NOT_FOUND =
            ApplicationError.of(
                    "PRODUCT_NOT_FOUND",
                    "Product does not exist"
            );

    private static final ApplicationError CODE_ALREADY_EXISTS =
            ApplicationError.of(
                    "SKU_CODE_ALREADY_EXISTS",
                    "A SKU with this code already exists"
            );

    private final ProductRepository products;
    private final SkuRepository skus;
    private final EventPublisher eventPublisher;

    public CreateSkuHandler(
            ProductRepository products,
            SkuRepository skus,
            EventPublisher eventPublisher
    ) {
        this.products = Objects.requireNonNull(products);
        this.skus = Objects.requireNonNull(skus);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<SkuId>> handle(CreateSkuCommand command) {
        return Uni.createFrom()
                .completionStage(products.findById(command.productId()))
                .onItem()
                .transformToUni(maybeProduct -> {
                    if (maybeProduct.isEmpty()) {
                        return Uni.createFrom().item(
                                Result.failure(PRODUCT_NOT_FOUND)
                        );
                    }

                    return Uni.createFrom()
                            .completionStage(
                                    skus.existsByCode(command.code())
                            )
                            .onItem()
                            .transformToUni(exists -> exists
                                    ? Uni.createFrom().item(
                                            Result.failure(
                                                    CODE_ALREADY_EXISTS
                                            )
                                    )
                                    : create(command));
                });
    }

    private Uni<Result<SkuId>> create(CreateSkuCommand command) {
        var sku = Sku.create(
                SkuId.generate(),
                command.productId(),
                command.variantId(),
                command.code(),
                command.name()
        );

        return Uni.createFrom()
                .completionStage(skus.save(sku))
                .onItem()
                .transformToUni(saved -> eventPublisher
                        .publish(saved.pullDomainEvents())
                        .replaceWith(Result.success(saved.id())));
    }
}