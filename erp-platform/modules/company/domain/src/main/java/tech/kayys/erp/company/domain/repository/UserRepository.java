package tech.kayys.erp.company.domain.repository;

import tech.kayys.erp.foundation.domain.Repository;
import tech.kayys.erp.company.domain.identifier.CompanyId;
import tech.kayys.erp.company.domain.identifier.RoleId;
import tech.kayys.erp.company.domain.identifier.UserId;
import tech.kayys.erp.company.domain.model.User;
import tech.kayys.erp.company.domain.valueobject.UserStatus;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Repository for User aggregates.
 */
public interface UserRepository extends Repository<User, UserId> {

    /**
     * Finds users by company.
     */
    CompletionStage<List<User>> findByCompanyId(CompanyId companyId);

    /**
     * Finds users by status.
     */
    CompletionStage<List<User>> findByStatus(UserStatus status);

    /**
     * Finds active users in a company.
     */
    default CompletionStage<List<User>> findActiveUsers(CompanyId companyId) {
        return findByCompanyId(companyId)
            .thenApply(users -> users.stream()
                .filter(User::isAccessible)
                .toList()
            );
    }

    /**
     * Finds users by email.
     */
    CompletionStage<User> findByEmail(String email);

    /**
     * Finds users by username.
     */
    CompletionStage<User> findByUsername(String username);

    /**
     * Finds users by role.
     */
    CompletionStage<List<User>> findByRoleId(RoleId roleId);

    /**
     * Finds users by role and company.
     */
    CompletionStage<List<User>> findByRoleIdAndCompany(RoleId roleId, CompanyId companyId);

    /**
     * Finds users with a specific permission.
     */
    CompletionStage<List<User>> findByPermission(String permission);

    /**
     * Checks if a username is available.
     */
    CompletionStage<Boolean> isUsernameAvailable(String username);

    /**
     * Checks if an email is available.
     */
    CompletionStage<Boolean> isEmailAvailable(String email);
}
<modules>
    <!-- Foundation -->
    <module>foundation/domain</module>
    <module>foundation/application</module>
    <module>foundation/reactive-mutiny</module>

    <!-- Architecture Tests -->
    <module>architecture/tests</module>

    <!-- Business Modules -->
    <module>modules/catalog/domain</module>
    <module>modules/catalog/application</module>
    <module>modules/catalog/infrastructure</module>
    <module>modules/catalog/interfaces</module>

    <module>modules/sales/domain</module>
    <module>modules/sales/application</module>
    <module>modules/sales/infrastructure</module>
    <module>modules/sales/interfaces</module>

    <module>modules/pricing/domain</module>
    <module>modules/pricing/application</module>
    <module>modules/pricing/infrastructure</module>
    <module>modules/pricing/interfaces</module>

    <module>modules/subscription/domain</module>
    <module>modules/subscription/application</module>
    <module>modules/subscription/infrastructure</module>
    <module>modules/subscription/interfaces</module>

    <module>modules/accounting/domain</module>
    <module>modules/accounting/application</module>
    <module>modules/accounting/infrastructure</module>
    <module>modules/accounting/interfaces</module>

    <module>modules/purchasing/domain</module>
    <module>modules/purchasing/application</module>
    <module>modules/purchasing/infrastructure</module>
    <module>modules/purchasing/interfaces</module>

    <module>modules/promotion/domain</module>
    <module>modules/promotion/application</module>
    <module>modules/promotion/infrastructure</module>
    <module>modules/promotion/interfaces</module>

    <module>modules/company/domain</module>
    <module>modules/company/application</module>
    <module>modules/company/infrastructure</module>
    <module>modules/company/interfaces</module>
</modules>
// Add to existing CompleteArchitectureTest class:

@ArchTest
static final ArchRule companyDomainMustNotDependOnOtherContexts =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.company.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "tech.kayys.erp.catalog..",
                        "tech.kayys.erp.sales..",
                        "tech.kayys.erp.inventory..",
                        "tech.kayys.erp.accounting.."
                );

@ArchTest
static final ArchRule companyDomainPackagesCorrect =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.company.domain..")
                .should()
                .resideInAnyPackage(
                        "tech.kayys.erp.company.domain.model..",
                        "tech.kayys.erp.company.domain.identifier..",
                        "tech.kayys.erp.company.domain.valueobject..",
                        "tech.kayys.erp.company.domain.repository.."
                );

@ArchTest
static final ArchRule userStatusStateMachine =
        classes()
                .that()
                .resideInAPackage("tech.kayys.erp.company.domain.valueobject..")
                .and()
                .haveSimpleName("UserStatus")
                .should()
                .haveOnlyFinalFields()
                .andShould()
                .haveMethod("canTransitionTo");

@ArchTest
static final ArchRule companyDomainShouldBeFrameworkFree =
        noClasses()
                .that()
                .resideInAPackage("tech.kayys.erp.company.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "io.quarkus..",
                        "jakarta.persistence..",
                        "org.hibernate.."
                );