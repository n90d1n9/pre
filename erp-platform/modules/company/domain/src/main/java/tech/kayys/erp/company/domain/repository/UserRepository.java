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
