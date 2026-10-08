package tech.kayys.syirkah.workforce.application.command;

import tech.kayys.syirkah.foundation.application.command.Command;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountPurpose;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountType;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.Objects;

public record AddPaymentAccountCommand(
        WorkerId workerId,
        PaymentAccountType type,
        PaymentAccountPurpose purpose,
        String accountName,
        String accountNumber,
        String providerCode
) implements Command {
    public AddPaymentAccountCommand {
        Objects.requireNonNull(workerId, "workerId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(purpose, "purpose must not be null");
        Objects.requireNonNull(accountName, "accountName must not be null");
        Objects.requireNonNull(accountNumber, "accountNumber must not be null");
        Objects.requireNonNull(providerCode, "providerCode must not be null");
    }
}
