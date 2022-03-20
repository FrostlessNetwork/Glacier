package network.frostless.glacier.scoreboard.core;

import org.bukkit.entity.Player;

public abstract class FrostBoard extends FrostFastBoard {

    public FrostBoard(Player player, String objectiveName) {
        super(player);
        updateTitle(objectiveName);
    }

    public abstract void updateBoard();
}

