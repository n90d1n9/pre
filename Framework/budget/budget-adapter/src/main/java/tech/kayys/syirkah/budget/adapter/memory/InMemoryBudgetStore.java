package tech.kayys.syirkah.budget.adapter.memory;

import tech.kayys.syirkah.budget.domain.Budget;
import tech.kayys.syirkah.budget.spi.port.BudgetStorePort;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory adapter for operational budgets. */
public final class InMemoryBudgetStore implements BudgetStorePort {
    private final ConcurrentHashMap<String, Budget> budgets = new ConcurrentHashMap<>();

    @Override
    public Optional<Budget> find(String budgetId) {
        return Optional.ofNullable(budgets.get(budgetId));
    }

    @Override
    public void save(Budget budget) {
        budgets.put(budget.budgetId(), budget);
    }
}
