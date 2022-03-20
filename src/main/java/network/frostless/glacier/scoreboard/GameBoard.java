package network.frostless.glacier.scoreboard;

import network.frostless.glacier.scoreboard.core.FrostBoard;
import org.bukkit.entity.Player;

public abstract class GameBoard extends FrostBoard {

    public GameBoard(Player player, String objectiveName) {
        super(player, objectiveName);
    }

}
