package network.frostless.glacier.game.events;

import lombok.Getter;
import network.frostless.glacier.utils.Pair;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.event.GameEvent;
import network.frostless.glacierapi.game.event.GameEventManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class DefaultGameEventManager<G extends Game<?, ?>> implements GameEventManager<G> {

    @Getter
    private final Queue<GameEvent<G>> events = new ArrayDeque<>();

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> future;

    private GameEvent<G> currentEvent;

    private long startEventTime;

    private final G game;

    public DefaultGameEventManager(G game) {

        this.game = game;
    }

    public void start() {
        if (future != null) {
            future.cancel(true);
        }

        future = executorService.scheduleAtFixedRate(this::tick, 0, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    private void tick() {
        if (events.isEmpty()) {
            future.cancel(true);
            return;
        }

        if (currentEvent == null) {
            currentEvent = next();
            startEventTime = System.currentTimeMillis();
        }

        Pair<Integer, TimeUnit> timePair = currentEvent.getDelay();
        long delayInMilliseconds = timePair.getValue().toMillis(timePair.getKey());

        if (System.currentTimeMillis() - startEventTime >= delayInMilliseconds) {
            currentEvent.execute(game);

            currentEvent = next();
            startEventTime = System.currentTimeMillis();
        }
    }

    @Override
    public @Nullable GameEvent<G> getCurrentEvent() {
        return currentEvent;
    }

    @Override
    public @Nullable GameEvent<G> next() {
        return events.poll();
    }

    @Override
    public LinkedList<GameEvent<G>> getRemainingEvents() {
        return (LinkedList<GameEvent<G>>) events;
    }

    @Override
    public void addEvent(GameEvent<G> event) {
        events.add(event);
    }

    @Override
    public void removeEvent(GameEvent<G> event) {
        events.remove(event);
    }
}
