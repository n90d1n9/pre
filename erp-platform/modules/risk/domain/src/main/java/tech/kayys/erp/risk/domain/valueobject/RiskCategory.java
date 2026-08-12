package tech.kayys.erp.risk.domain.valueobject;

/**
 * Categories of risks.
 */
public enum RiskCategory {
    STRATEGIC("Strategic - affecting business strategy"),
    OPERATIONAL("Operational - affecting daily operations"),
    FINANCIAL("Financial - affecting financial performance"),
    COMPLIANCE("Compliance - regulatory and legal risks"),
    REPUTATIONAL("Reputational - affecting brand and reputation"),
    CYBERSECURITY("Cybersecurity - IT and data security risks"),
    HUMAN_RESOURCES("Human Resources - workforce-related risks"),
    SUPPLY_CHAIN("Supply Chain - vendor and logistics risks"),
    NATURAL_DISASTER("Natural Disaster - environmental risks"),
    POLITICAL("Political - geopolitical risks"),
    TECHNOLOGY("Technology - system and technology risks"),
    HEALTH_SAFETY("Health & Safety - workplace safety risks");

    private final String description;

    RiskCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}