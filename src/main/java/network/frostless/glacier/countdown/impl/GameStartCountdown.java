package network.frostless.glacier.countdown.impl;

import net.kyori.adventure.audience.MessageType;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.countdown.GameCountdown;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Sound;

/**
 * An implementation of {@link GameCountdown} which allows for default
 * map loading when there are enough users.
 *
 * @param <U> The type of {@link GameUser}
 * @param <T> The type of {@link Team}
 */
public class GameStartCountdown<U extends GameUser, T extends Team<U>> extends GameCountdown<U, T> {

    /**
     * Creates a new game countdown.
     *
     * @param timer      The amount of time in seconds that the countdown will last.
     * @param minPlayers The minimum amount of players that must be in the game to start the countdown.
     * @param game       The game that the countdown is for.
     */
    public GameStartCountdown(int timer, int minPlayers, Game<U, T> game) {
        super(timer, minPlayers, game);
    }

    @Override
    public void start() {
        getGame().executeUsers(u -> {
            u.setUserState(UserGameState.INGAME);
            u.onStartGame();
        });
        getGame().setGameState(GameState.INGAME);
    }


    @Override
    public void onEnoughPlayers() {
        getGame().broadcast("&eStarting game in &c" + getTimer() + "&e seconds.");
        getGame().executePlayers(p -> p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1));
    }

    @Override
    public void onCancel() {
        getGame().executePlayers(p -> p.sendMessage(Glacier.miniMessage.deserialize("<yellow>Game start cancelled."), MessageType.SYSTEM));
    }

    @Override
    public void onCount(int curr) {
        getGame().executePlayers(p -> p.setLevel(getTimer() - curr));
        if (curr % 10 == 0 || curr >= getTimer() - 10) {
            getGame().executePlayers(p -> {
                int time = getTimer() - curr;
                p.sendMessage("&eStarting in &c" + time + "&e seconds.");
                p.playSound(p.getLocation(), "BLOCK_NOTE_BLOCK_PLING", 1, 1);
                p.sendMessage(Glacier.miniMessage.deserialize("<green>Game is starting in <aqua>" + time + " <green>seconds!"), MessageType.SYSTEM);
            });

        }
    }
}
