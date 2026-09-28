package tech.kayys.syirkah.budget.domain;

public record CommitmentId(String value) {
    public CommitmentId {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("commitment id must not be blank");
    }
}
