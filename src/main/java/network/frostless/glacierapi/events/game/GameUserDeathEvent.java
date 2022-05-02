package network.frostless.glacierapi.events.game;

import network.frostless.glacierapi.events.GameUserEvent;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GameUserDeathEvent extends GameUserEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a user joins a game.
     */
    public GameUserDeathEvent(@NotNull final GameUser who) {
        super(who);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

}
