package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollPeriodId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRun;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;

import java.util.List;
import java.util.concurrent.CompletionStage;

public interface PayrollRunRepository extends Repository<PayrollRun, PayrollRunId> {

    CompletionStage<List<PayrollRun>> findByPeriod(PayrollPeriodId periodId);
}
