package network.frostless.glacierapi.events.game;

import network.frostless.glacierapi.events.GameUserEvent;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


/**
 * Called when a user joins a lobby.
 */
public class LobbyJoinEvent extends GameUserEvent {

    private static final HandlerList handlers = new HandlerList();

    public LobbyJoinEvent(@NotNull final GameUser who) {
        super(who);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
