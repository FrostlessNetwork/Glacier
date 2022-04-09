package network.frostless.glacierapi.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public interface Game<U extends GameUser, T extends Team<U>> extends Minigame {

    /* Game state */
    GameState getGameState();
    void setGameState(GameState state);

    long getStartTime();
    void setStartTime(long time);

    List<U> getPlayers();

    /**
     * Calls the given consumer for each Bukkit {@link Player} in the game.
     * @param callback The consumer to call for each user.
     */
    default void executePlayers(Consumer<Player> callback) {
        getPlayers().forEach(user -> callback.accept(user.getPlayer()));
    }
    /**
     * Calls the given consumer for each Bukkit {@link Player} in the game.
     * @param callback The consumer to call for each user.
     */
    default void executeUsers(Consumer<U> callback) {
        getPlayers().forEach(callback);
    }

    /**
     * Broadcasts a message to all players in the game.
     * @param component The message to broadcast.
     */
    default void broadcast(Component component) {
        executeUsers(user -> user.getPlayer().sendMessage(component));
    }

    /**
     * Broadcasts a message to all players in the game.
     * @param message The message to broadcast.
     */
    default void broadcast(String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        executeUsers(user -> user.getPlayer().sendMessage(component));
    }
}
