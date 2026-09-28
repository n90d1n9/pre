package tech.kayys.syirkah.workforce.domain.skill;

/**
 * Lifecycle status of a Skill master definition.
 */
public enum SkillStatus {
    ACTIVE,
    INACTIVE;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
