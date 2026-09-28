package tech.kayys.syirkah.budget.spi.port;

import tech.kayys.syirkah.budget.domain.BudgetId;
import tech.kayys.syirkah.budget.domain.PlanningBudget;

import java.util.Optional;

public interface BudgetRepository {
    PlanningBudget save(PlanningBudget budget);
    Optional<PlanningBudget> find(BudgetId id);
}
