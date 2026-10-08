package tech.kayys.syirkah.support.application.api;

import io.smallrye.mutiny.Uni;

public interface SupportActorProvider {

    Uni<ActorContext> currentActor();
}
