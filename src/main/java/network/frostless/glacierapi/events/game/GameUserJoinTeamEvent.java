package network.frostless.glacierapi.events.game;

import lombok.Getter;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.events.GameUserEvent;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class GameUserJoinTeamEvent extends GameUserEvent {
    private static final HandlerList handlers = new HandlerList();

    private final Team<? extends GameUser> team;

    /**
     * Called when a user joins a game.
     */
    public GameUserJoinTeamEvent(@NotNull final GameUser who, @NotNull final Team<? extends GameUser> team) {
        super(who);
        this.team = team;
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
