package tech.kayys.syirkah.budget.application;

import tech.kayys.syirkah.budget.spi.port.BudgetRepository;
import tech.kayys.syirkah.budget.domain.*;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class BudgetLifecycleService {
    private final BudgetRepository budgets;
    public BudgetLifecycleService(BudgetRepository budgets) { this.budgets = Objects.requireNonNull(budgets); }
    public PlanningBudget create(BudgetId id, String code, int fiscalYear) {
        if (budgets.find(id).isPresent()) throw new BudgetViolationException("budget already exists: " + id);
        return budgets.save(new PlanningBudget(id, code, fiscalYear));
    }
    public PlanningBudget require(BudgetId id) {
        return budgets.find(id).orElseThrow(() -> new NoSuchElementException("unknown budget: " + id));
    }
}
