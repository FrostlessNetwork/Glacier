package network.frostless.glacierapi.user;

import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacierapi.user.loader.UserLoaderResult;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {

    /**
     * Called when a player's data needs to be loaded.
     * @param player The player to load
     * @implNote This method is SYNC.
     */
    void loadUser(Player player, GlobalUser user);

    /**
     * Called when a user disconnects.
     * @param player The player that disconnected
     * @implNote This method is async.
     */
    void unloadUser(Player player);

    <T> T getOrCreate(UUID uuid);

    List<GameUser> getUsers();

    void registerConnection();

    /**
     * Called when a user logs in.
     * @param user The user that logged in
     * @return If the player is allowed to log in
     * @implNote This method is async.
     */
    CompletableFuture<UserLoaderResult> verifyUser(GlobalUser user);
}
