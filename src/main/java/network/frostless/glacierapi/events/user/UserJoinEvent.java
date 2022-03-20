package network.frostless.glacierapi.events.user;

import lombok.Getter;
import network.frostless.glacierapi.events.GameUserEvent;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserJoinEvent extends GameUserEvent {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    private final GameUser player;

    public UserJoinEvent(@NotNull GameUser who) {
        super(who);
        this.player = who;
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
