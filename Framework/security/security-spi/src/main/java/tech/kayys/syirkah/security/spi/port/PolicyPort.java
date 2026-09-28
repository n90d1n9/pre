package tech.kayys.syirkah.security.spi.port;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.security.domain.authorization.Permission;
import tech.kayys.syirkah.security.domain.authorization.Principal;

import java.util.Set;

/**
 * Loads the grants effective for a principal.
 *
 * <p>The evaluator stays a pure domain service; where grants come from
 * (role store, IAM provider, feature flags) is infrastructure.
 */
public interface PolicyPort {

    Uni<Set<Permission>> permissionsFor(Principal principal);
}
