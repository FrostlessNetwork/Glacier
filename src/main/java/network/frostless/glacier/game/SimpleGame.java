package network.frostless.glacier.game;

import lombok.Data;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.World;

@Data
public abstract class SimpleGame<U extends GameUser, T extends Team<U>> implements Game<U, T> {

    private GameState gameState;

    private int maxPlayers;

    private String identifier;

    private World world;

    private long startTime;


    public SimpleGame() {
        gameState = GameState.WAITING;
    }


}
