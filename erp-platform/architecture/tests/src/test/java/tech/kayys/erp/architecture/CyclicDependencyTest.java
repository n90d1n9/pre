package tech.kayys.erp.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.junit5.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * Tests for cyclic dependencies between modules.
 */
public class CyclicDependencyTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
                .importPackages("tech.kayys.erp");
    }

    @ArchTest
    static final ArchRule noCyclicDependenciesBetweenModules =
            noClasses()
                    .that()
                    .resideInAPackage("tech.kayys.erp.modules..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp.modules..")
                    .andShould()
                    .dependOnClassesThat()
                    .resideInAPackage("tech.kayys.erp.modules..")
                    .andShould()
                    .haveNameMatching(".*");
}
