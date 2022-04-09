package network.frostless.glacier.countdown;

import lombok.Getter;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.user.GameUser;

/**
 * An base for all countdowns that are used ingame. This
 * allows you to easily create new countdowns that are interacting
 * with the game users and such.
 * @author RiceCX
 * @param <U> The type of user that is being used.
 * @param <T> The type of team that is being used.
 */
@Getter
public abstract class GameCountdown<U extends GameUser, T extends Team<U>> implements Countdown {

    private final int timer;

    private final int minPlayers;

    private final Game<U, T> game;

    /**
     * Creates a new game countdown.
     * @param timer The amount of time in seconds that the countdown will last.
     * @param minPlayers The minimum amount of players that must be in the game to start the countdown.
     * @param game The game that the countdown is for.
     */
    public GameCountdown(int timer, int minPlayers, Game<U, T> game) {
        this.timer = timer;
        this.minPlayers = minPlayers;
        this.game = game;
    }

    public void onEnoughPlayers() {
        // Override this to do something when enough players are in the game.
    }
}
