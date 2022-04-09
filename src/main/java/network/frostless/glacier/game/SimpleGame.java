package network.frostless.glacier.game;

import lombok.Data;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.user.GameUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;

@Data
public abstract class SimpleGame<U extends GameUser, T extends Team<U>> implements Game<U, T> {

    protected Logger logger = LogManager.getLogger("Game " + getIdentifier());

    private GameState gameState;

    private int maxPlayers;

    private String identifier;

    private World world;

    private long startTime;


    public SimpleGame() {
        gameState = GameState.WAITING;
    }


    /**
     * Called when the game is needed to be started.
     */
    public abstract void start();

    public abstract void stop();

}
