package tech.kayys.syirkah.workforce.application.command;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.foundation.application.command.CommandHandler;
import tech.kayys.syirkah.foundation.application.event.EventPublisher;
import tech.kayys.syirkah.foundation.application.result.ApplicationError;
import tech.kayys.syirkah.foundation.application.result.Result;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccount;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountId;
import tech.kayys.syirkah.workforce.spi.port.PaymentAccountRepository;
import tech.kayys.syirkah.workforce.spi.port.WorkerRepository;

import java.util.Objects;

public class AddPaymentAccountHandler implements CommandHandler<AddPaymentAccountCommand, Result<PaymentAccountId>> {

    private final WorkerRepository workerRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final EventPublisher eventPublisher;

    public AddPaymentAccountHandler(
            WorkerRepository workerRepository,
            PaymentAccountRepository paymentAccountRepository,
            EventPublisher eventPublisher
    ) {
        this.workerRepository = Objects.requireNonNull(workerRepository);
        this.paymentAccountRepository = Objects.requireNonNull(paymentAccountRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public Uni<Result<PaymentAccountId>> handle(AddPaymentAccountCommand cmd) {
        return Uni.createFrom().completionStage(workerRepository.findById(cmd.workerId()))
                .chain(optWorker -> {
                    if (optWorker.isEmpty()) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("WORKER_NOT_FOUND", "Worker not found")));
                    }
                    PaymentAccount account;
                    try {
                        account = PaymentAccount.create(
                                PaymentAccountId.generate(),
                                cmd.workerId(),
                                cmd.type(),
                                cmd.purpose(),
                                cmd.accountName(),
                                cmd.accountNumber(),
                                cmd.providerCode()
                        );
                    } catch (Exception e) {
                        return Uni.createFrom().item(Result.failure(ApplicationError.of("INVALID_PAYMENT_ACCOUNT", e.getMessage())));
                    }
                    return Uni.createFrom().completionStage(paymentAccountRepository.save(account))
                            .chain(saved -> eventPublisher.publish(saved.pullDomainEvents())
                                    .replaceWith(Result.success(saved.getId())));
                });
    }
}
