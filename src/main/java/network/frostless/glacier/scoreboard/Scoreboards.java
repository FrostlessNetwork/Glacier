package network.frostless.glacier.scoreboard;

import com.google.common.collect.Maps;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.scoreboard.core.FrostBoard;
import network.frostless.glacier.utils.GlobalTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Scoreboards implements Listener {

    private final Map<UUID, FrostBoard> boards = Maps.newConcurrentMap();
    private final Logger logger = LogManager.getLogger("Scoreboard Manager");
    private final GlobalTimer globalTimer;


    public Scoreboards() {
        Glacier.getPlugin().registerListeners(this);

        globalTimer = new GlobalTimer(1, 1, TimeUnit.SECONDS, this::updateBoards).start();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent evt) {
        removeScoreboard(evt.getPlayer());
    }

    public void provideScoreboard(Player player, FrostBoard board) {
        boards.put(player.getUniqueId(), board);
        board.updateBoard();
        logger.info("Providing " + board.getTitle() + " to " + player.getName());
    }

    public void removeScoreboard(Player player) {
        FrostBoard board = boards.remove(player.getUniqueId());

        if(board != null) {
            board.delete();
        }
    }

    /**
     * Terminate the update task
     */
    private void pauseScoreboards() {
        globalTimer.terminate();
    }

    /**
     * Update boards
     */
    private void updateBoards() {
        for (FrostBoard board : boards.values()) {
            board.updateBoard();
        }
    }

}
