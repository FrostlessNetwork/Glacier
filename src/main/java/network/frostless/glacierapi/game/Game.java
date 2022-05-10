package network.frostless.glacierapi.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import network.frostless.glacierapi.game.event.GameEventManager;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.map.MapMeta;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

public interface Game<U extends GameUser, T extends Team<U>> extends Minigame {

    GameEventManager<Game<U, T>> getEventManager();

    void setMapMeta(MapMeta map);

    MapMeta getMapMeta();

    void setWorld(World world);

    World getWorld();

    List<T> getTeams();

    @SuppressWarnings("unchecked")
    default <V extends MapMeta> V mapMeta() {
        return (V) getMapMeta();
    }


    GameMechanicHandler<U> getMechanicHandler();

    /* Game state */

    /**
     * Called when the game gets it's identifier.
     */
    void onReady();

    GameState getGameState();

    void setGameState(GameState state);

    long getStartTime();

    void setStartTime(long time);

    int getMaxPlayers();

    int getMinPlayers();

    List<U> getPlayers();

    int getIngamePlayers();

    int getSpectatingPlayers();

    Team<U> getTeamForPlayer(U player);


    void forceStart();

    /**
     * Called when the game is needed to be started.
     */
    void start();

    void stop();


    void applyMapMapper(MapMeta mapMeta);

    Location getWorldCenter();

    /**
     * Calls the given consumer for each Bukkit {@link Player} in the game.
     *
     * @param callback The consumer to call for each user.
     */
    default void executePlayers(Consumer<Player> callback) {
        getPlayers().forEach(user -> callback.accept(user.getPlayer()));
    }

    /**
     * Calls the given consumer for each Bukkit {@link Player} in the game.
     *
     * @param callback The consumer to call for each user.
     */
    default void executeUsers(Consumer<U> callback) {
        getPlayers().forEach(callback);
    }

    /**
     * Broadcasts a message to all players in the game.
     *
     * @param component The message to broadcast.
     */
    default void broadcast(Component component) {
        executeUsers(user -> user.getPlayer().sendMessage(component));
    }

    /**
     * Broadcasts a message to all players in the game.
     *
     * @param message The message to broadcast.
     */
    default void broadcast(String message) {
        Component component = MiniMessage.miniMessage().deserialize(message);
        executeUsers(user -> user.getPlayer().sendMessage(component));
    }

    void addPlayer(U user);

    void removePlayer(U user);

}
