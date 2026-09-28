package tech.kayys.syirkah.workforce.spi.port;

import tech.kayys.syirkah.foundation.domain.repository.Repository;
import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;
import tech.kayys.syirkah.workforce.domain.payslip.Payslip;
import tech.kayys.syirkah.workforce.domain.payslip.PayslipId;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public interface PayslipRepository extends Repository<Payslip, PayslipId> {

    CompletionStage<Optional<Payslip>> findByEmploymentAndRun(EmploymentId employmentId, PayrollRunId payrollRunId);

    CompletionStage<List<Payslip>> findByRun(PayrollRunId payrollRunId);
}
