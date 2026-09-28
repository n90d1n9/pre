package tech.kayys.syirkah.finance.treasury.domain.repository;

import tech.kayys.syirkah.finance.treasury.domain.identifier.DrawerSessionId;
import tech.kayys.syirkah.finance.treasury.domain.model.CashDrawerSession;
import tech.kayys.syirkah.foundation.domain.repository.Repository;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface CashDrawerSessionRepository extends Repository<CashDrawerSession, DrawerSessionId> {
    CompletionStage<Optional<CashDrawerSession>> findActiveSessionByRegister(String registerId);
}
