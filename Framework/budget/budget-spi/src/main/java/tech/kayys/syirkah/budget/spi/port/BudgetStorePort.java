package tech.kayys.syirkah.budget.spi.port;

import tech.kayys.syirkah.budget.domain.Budget;

import java.util.Optional;

/** Persistence port for the operational budget aggregate. */
public interface BudgetStorePort {
    Optional<Budget> find(String budgetId);

    void save(Budget budget);
}
