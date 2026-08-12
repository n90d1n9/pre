package tech.kayys.erp.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit5.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Tests for modular architecture and bounded context isolation.
 */
public class ModularArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .importPackages("tech.kayys.erp");
    }

    @ArchTest
    static final ArchRule modulesShouldNotShareEntities =
            noClasses()
                    .that()
                    .resideInAnyPackage(
                            "tech.kayys.erp.catalog..",
                            "tech.kayys.erp.sales..",
                            "tech.kayys.erp.inventory..",
                            "tech.kayys.erp.accounting.."
                    )
                    .should()
                    .haveFullyQualifiedName(
                            "tech.kayys.erp.catalog.domain.model.Product"
                    );

    @ArchTest
    static final ArchRule foundationShouldNotContainBusinessLogic =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp.foundation..")
                    .should()
                    .haveNameMatching(".*Service.*")
                    .orShould()
                    .haveNameMatching(".*Handler.*")
                    .orShould()
                    .haveNameMatching(".*Factory.*");

    @ArchTest
    static final ArchRule domainEventsShouldBeImmutable =
            classes()
                    .that()
                    .implement(tech.kayys.erp.foundation.domain.DomainEvent.class)
                    .should()
                    .haveOnlyFinalFields();

    @ArchTest
    static final ArchRule commandsShouldImplementCommandInterface =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Command")
                    .should()
                    .implement(tech.kayys.erp.foundation.application.Command.class);

    @ArchTest
    static final ArchRule queriesShouldImplementQueryInterface =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Query")
                    .should()
                    .implement(tech.kayys.erp.foundation.application.Query.class);

    @ArchTest
    static final ArchRule aggregateIdsShouldExtendIdentifier =
            classes()
                    .that()
                    .haveSimpleNameEndingWith("Id")
                    .and()
                    .areNotEnums()
                    .should()
                    .extend(tech.kayys.erp.foundation.domain.Identifier.class);
}
