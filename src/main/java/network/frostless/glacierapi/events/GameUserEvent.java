package network.frostless.glacierapi.events;

import lombok.Getter;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class GameUserEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    protected GameUser gameUser;

    public GameUserEvent(@NotNull final GameUser who) {
        this.gameUser = who;
    }

    GameUserEvent(@NotNull final GameUser who, boolean async) {
        super(async);
        gameUser = who;
    }

    public <T extends GameUser> T to(@NotNull Class<T> clazz) {
        return clazz.cast(gameUser);
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
