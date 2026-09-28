package tech.kayys.syirkah.budget.domain;

/** Outcome of budget availability control checks. */
public enum BudgetControlOutcome {
    ALLOW, WARN, BLOCK, APPROVAL_REQUIRED
}
