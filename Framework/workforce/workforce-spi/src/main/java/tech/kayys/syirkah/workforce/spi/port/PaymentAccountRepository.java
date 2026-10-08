package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccount;
import tech.kayys.syirkah.workforce.domain.paymentaccount.PaymentAccountId;
import tech.kayys.syirkah.workforce.domain.worker.WorkerId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PaymentAccountRepository extends Repository<PaymentAccount, PaymentAccountId> {

    CompletionStage<List<PaymentAccount>> findByWorker(WorkerId workerId);
}
