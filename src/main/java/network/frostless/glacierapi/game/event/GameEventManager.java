package network.frostless.glacierapi.game.event;

import network.frostless.glacierapi.game.Game;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

public interface GameEventManager<G extends Game<?, ?>> {

    Queue<GameEvent<G>> getEvents();

    @Nullable
    GameEvent<G> getCurrentEvent();

    @Nullable
    GameEvent<G> next();

    LinkedList<GameEvent<G>> getRemainingEvents();

    void addEvent(GameEvent<G> event);

    void removeEvent(GameEvent<G> event);

    void start();
}