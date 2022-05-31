package network.frostless.glacier.countdown.impl;

import com.grinderwolf.swm.api.world.SlimeWorld;
import net.kyori.adventure.audience.MessageType;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;

public class DefaultGameStartCountdown<U extends GameUser, T extends Team<U>> extends GameStartCountdown<U, T> {

    /**
     * Creates a new game countdown.
     *
     * @param timer      The amount of time in seconds that the countdown will last.
     * @param minPlayers The minimum amount of players that must be in the game to start the countdown.
     * @param game       The game that the countdown is for.
     */
    public DefaultGameStartCountdown(int timer, int minPlayers, Game<U, T> game) {
        super(timer, minPlayers, game);
    }

    @Override
    protected void onGeneratedWorld(SlimeWorld world) {
        OffloadTask.offloadSync(() -> {
            getGame().executeUsers(u -> {
                u.sendMessage("<yellow>Game is now starting!");
                u.setUserState(UserGameState.INGAME);
                OffloadTask.offloadSync(u::onStartGame);
            });
            getGame().setGameState(GameState.INGAME);
            getGame().start();
        });
    }
}
