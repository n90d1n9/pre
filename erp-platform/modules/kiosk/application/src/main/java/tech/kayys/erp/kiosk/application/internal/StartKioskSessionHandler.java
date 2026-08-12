package tech.kayys.erp.kiosk.application.internal;

import tech.kayys.erp.foundation.application.CommandHandler;
import tech.kayys.erp.foundation.application.UseCase;
import tech.kayys.erp.kiosk.application.api.command.StartKioskSessionCommand;
import tech.kayys.erp.kiosk.domain.identifier.KioskSessionId;
import tech.kayys.erp.kiosk.domain.model.KioskDevice;
import tech.kayys.erp.kiosk.domain.model.KioskSession;
import tech.kayys.erp.kiosk.domain.repository.KioskDeviceRepository;
import tech.kayys.erp.kiosk.domain.repository.KioskSessionRepository;
import tech.kayys.erp.sales.domain.model.Cart;
import tech.kayys.erp.sales.domain.repository.CartRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Handler for starting kiosk sessions.
 */
@UseCase("Start a kiosk session")
public class StartKioskSessionHandler
        implements CommandHandler<StartKioskSessionCommand, KioskSessionId> {

    private final KioskDeviceRepository kioskDeviceRepository;
    private final KioskSessionRepository kioskSessionRepository;
    private final CartRepository cartRepository;

    @Inject
    public StartKioskSessionHandler(
            KioskDeviceRepository kioskDeviceRepository,
            KioskSessionRepository kioskSessionRepository,
            CartRepository cartRepository) {
        this.kioskDeviceRepository = kioskDeviceRepository;
        this.kioskSessionRepository = kioskSessionRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public CompletionStage<KioskSessionId> handle(StartKioskSessionCommand command) {
        // 1. Validate kiosk exists and is available
        return kioskDeviceRepository.findById(command.kioskId())
            .thenCompose(kioskOpt -> {
                if (kioskOpt.isEmpty()) {
                    return CompletableFuture.failedFuture(
                        new IllegalArgumentException("Kiosk not found: " + command.kioskId())
                    );
                }

                KioskDevice kiosk = kioskOpt.get();

                if (!kiosk.isActive() || !kiosk.getStatus().isOperational()) {
                    return CompletableFuture.failedFuture(
                        new IllegalStateException("Kiosk is not available: " + kiosk.getStatus())
                    );
                }

                // 2. Create a new cart
                Cart cart = Cart.create(
                    UUID.randomUUID(),
                    command.customerId() != null ? 
                        java.util.UUID.fromString(command.customerId()) : null
                );

                // 3. Save the cart
                return cartRepository.save(cart)
                    .thenCompose(savedCart -> {
                        // 4. Create the session
                        KioskSession session = KioskSession.create(
                            command.kioskSessionId(),
                            command.kioskId().getValue(),
                            command.language(),
                            command.currencyCode()
                        );

                        // 5. Set the cart ID
                        session.setCartId(savedCart.getId().getValue());

                        // 6. Set customer ID if provided
                        if (command.customerId() != null) {
                            session.setCustomerId(command.customerId());
                        }

                        // 7. Record session start interaction
                        Map<String, String> metadata = new HashMap<>();
                        metadata.put("language", command.language());
                        metadata.put("currency", command.currencyCode());
                        metadata.put("kioskMode", kiosk.getMode().name());

                        KioskSession.KioskInteraction interaction = new KioskSession.KioskInteraction(
                            UUID.randomUUID().toString(),
                            KioskSession.KioskInteractionType.SESSION_START,
                            "Session started at kiosk: " + kiosk.getDeviceName(),
                            metadata
                        );
                        session.addInteraction(interaction);

                        // 8. Save the session
                        return kioskSessionRepository.save(session)
                            .thenApply(KioskSession::getId);
                    });
            });
    }
}