package tech.kayys.syirkah.scheduling.adapter.memory;

import tech.kayys.syirkah.scheduling.domain.Schedule;
import tech.kayys.syirkah.scheduling.domain.ScheduleId;
import tech.kayys.syirkah.scheduling.spi.ScheduleRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryScheduleRepository implements ScheduleRepository {
    private final Map<ScheduleId, Schedule> store = new ConcurrentHashMap<>();
    public CompletionStage<Schedule> save(Schedule e) { store.put(e.getId(), e); return CompletableFuture.completedFuture(e); }
    public CompletionStage<Optional<Schedule>> findById(ScheduleId id) { return CompletableFuture.completedFuture(Optional.ofNullable(store.get(id))); }
    public CompletionStage<Boolean> existsById(ScheduleId id) { return CompletableFuture.completedFuture(store.containsKey(id)); }
    public CompletionStage<Void> delete(Schedule e) { store.remove(e.getId()); return CompletableFuture.completedFuture(null); }
    public CompletionStage<Void> deleteById(ScheduleId id) { store.remove(id); return CompletableFuture.completedFuture(null); }
}
