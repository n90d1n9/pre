package tech.kayys.syirkah.budget.domain;

/** Raised when a commitment would exceed the available budget. */
public class InsufficientBudgetException extends BudgetViolationException {
    public InsufficientBudgetException(String message) {
        super(message);
    }
}
