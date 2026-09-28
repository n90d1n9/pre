package tech.kayys.syirkah.workforce.adapter.memory;

import tech.kayys.syirkah.workforce.domain.employment.EmploymentId;
import tech.kayys.syirkah.workforce.domain.payroll.PayrollRunId;
import tech.kayys.syirkah.workforce.domain.payslip.Payslip;
import tech.kayys.syirkah.workforce.domain.payslip.PayslipId;
import tech.kayys.syirkah.workforce.spi.port.PayslipRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPayslipRepository implements PayslipRepository {

    private final Map<PayslipId, Payslip> store = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<Payslip> save(Payslip e) {
        store.put(e.getId(), e);
        return CompletableFuture.completedFuture(e);
    }

    @Override
    public CompletionStage<Optional<Payslip>> findById(PayslipId id) {
        return CompletableFuture.completedFuture(Optional.ofNullable(store.get(id)));
    }

    @Override
    public CompletionStage<Boolean> existsById(PayslipId id) {
        return CompletableFuture.completedFuture(store.containsKey(id));
    }

    @Override
    public CompletionStage<Void> delete(Payslip e) {
        store.remove(e.getId());
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Void> deleteById(PayslipId id) {
        store.remove(id);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<Optional<Payslip>> findByEmploymentAndRun(EmploymentId employmentId, PayrollRunId payrollRunId) {
        Optional<Payslip> opt = store.values().stream()
                .filter(p -> p.employmentId().equals(employmentId) && p.payrollRunId().equals(payrollRunId))
                .findFirst();
        return CompletableFuture.completedFuture(opt);
    }

    @Override
    public CompletionStage<List<Payslip>> findByRun(PayrollRunId payrollRunId) {
        List<Payslip> list = store.values().stream()
                .filter(p -> p.payrollRunId().equals(payrollRunId))
                .toList();
        return CompletableFuture.completedFuture(list);
    }
}
