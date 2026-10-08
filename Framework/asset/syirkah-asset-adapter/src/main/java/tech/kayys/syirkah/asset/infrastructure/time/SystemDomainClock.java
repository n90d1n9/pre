package tech.kayys.syirkah.asset.infrastructure.time;

import jakarta.enterprise.context.ApplicationScoped;
import tech.kayys.syirkah.foundation.domain.time.DomainClock;

import java.time.Instant;

/** Production {@link DomainClock} backed by the system clock. */
@ApplicationScoped
public class SystemDomainClock implements DomainClock {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
