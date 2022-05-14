package network.frostless.glacier.user.impl;

import lombok.Getter;
import lombok.Setter;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public abstract class GameUserImpl<G extends Game<? extends GameUser, ? extends Team<? extends GameUser>>> extends DataGameUserImpl {

    // Transient, no saves
    private transient String gameIdentifier;

    private transient UserGameState userState;

    private transient String rank;

    private transient Location spawnpoint;

    public GameUserImpl() {
    }

    public GameUserImpl(UUID uuid) {
        super(uuid);
    }

    @SuppressWarnings("unchecked")
    public G getGame() {
        return (G) super.getGame();
    }
}
