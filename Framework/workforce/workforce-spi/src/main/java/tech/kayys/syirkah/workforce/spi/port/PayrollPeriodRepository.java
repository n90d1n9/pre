package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.foundation.domain.tenant.TenantId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriod;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriodId;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PayrollPeriodRepository extends Repository<PayrollPeriod, PayrollPeriodId> {

    CompletionStage<Optional<PayrollPeriod>> findByPeriod(TenantId tenantId, LocalDate start, LocalDate end);
}
