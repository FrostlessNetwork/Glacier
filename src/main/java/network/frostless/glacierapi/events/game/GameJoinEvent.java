package network.frostless.glacierapi.events.game;

import lombok.Getter;
import network.frostless.glacierapi.events.GameUserEvent;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class GameJoinEvent extends GameUserEvent {

    private static final HandlerList handlers = new HandlerList();

    private final String gameServer;

    /**
     * Called when a user joins a game.
     */
    public GameJoinEvent(@NotNull final GameUser who, @NotNull String game) {
        super(who);
        this.gameServer = game;
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
