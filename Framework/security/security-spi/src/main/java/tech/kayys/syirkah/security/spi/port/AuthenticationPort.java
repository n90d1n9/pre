package tech.kayys.syirkah.security.spi.port;

import io.smallrye.mutiny.Uni;
import tech.kayys.syirkah.security.domain.authorization.Principal;

import java.util.Optional;

/**
 * "Who are you?" (base01.md §P1-17).
 *
 * <p>Implemented by password verification, OIDC token validation, mTLS or
 * an in-memory test double. The domain never sees a credential - it only
 * receives an authenticated {@link Principal}.
 */
public interface AuthenticationPort {

    Uni<Optional<Principal>> authenticate(String credential);
}
