package network.frostless.glacierapi.game.manager;

import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.user.GameUser;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface GameManager {

    Map<String, Object> getGames();

    /**
     * Creates a game and returns the specified game identifier
     * @param game The game to create
     * @param <U> The type of user
     * @param <T> The type of team
     * @return The game identifier
     */
    <U extends GameUser, T extends Team<U>> CompletableFuture<String> createGame(Game<U, T> game);


}