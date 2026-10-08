package tech.kayys.syirkah.crm.infrastructure.participant;

import tech.kayys.syirkah.crm.application.participant.ParticipantPort;
import tech.kayys.syirkah.ecosystem.application.port.ParticipantRepository;
import tech.kayys.syirkah.ecosystem.domain.identifier.ParticipantId;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Objects;
import java.util.concurrent.CompletionStage;

// Gated: ecosystem has no adapter module, so its outbound ParticipantRepository
// port has no CDI provider anywhere. Remove this gate once ecosystem ships an
// adapter that produces a ParticipantRepository bean.
@IfBuildProperty(
        name = "syirkah.crm.ecosystem-participant.enabled",
        stringValue = "false")
@ApplicationScoped
public class EcosystemParticipantAdapter implements ParticipantPort {

    private final ParticipantRepository participantRepository;

    public EcosystemParticipantAdapter(ParticipantRepository participantRepository) {
        this.participantRepository = Objects.requireNonNull(participantRepository);
    }

    @Override
    public CompletionStage<Boolean> exists(ParticipantId participantId) {
        Objects.requireNonNull(participantId, "participantId cannot be null");
        return participantRepository.findById(participantId)
                .map(java.util.Optional::isPresent)
                .subscribe()
                .asCompletionStage();
    }
}
