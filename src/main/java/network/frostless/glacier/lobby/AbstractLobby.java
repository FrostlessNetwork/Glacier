package network.frostless.glacier.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.events.game.LobbyJoinEvent;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract class AbstractLobby implements Listener, Lobby {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected Location location;

    /**
     * Constructs a new lobby with the provided world and location.
     *
     * @param spawn The location of the lobby.
     */
    public AbstractLobby(Location spawn) {
        this.location = spawn;
        Glacier.getPlugin().registerListeners(this);
    }

    protected abstract void onLobbyJoin(LobbyJoinEvent event);

    @EventHandler
    public void onPlayerJoin(LobbyJoinEvent event) {
        onLobbyJoin(event);
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if (isLobbyUser(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player player) {
            GameUser user = Users.getUser(player.getUniqueId(), GameUser.class);
            if (user != null && user.getUserState() != UserGameState.LOBBY) return;

            if (evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                executor.submit(() -> {
                    evt.getEntity().sendMessage(Component.text("No dying for you!").color(TextColor.color(0xAFFC09)));
                    evt.getEntity().teleportAsync(location).join();
                });
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if (evt.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (isLobbyUser(evt.getPlayer())) evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) if (isLobbyUser(player)) event.setCancelled(true);
    }

    private boolean isLobbyUser(Player player) {
        GameUser user = Users.getUser(player.getUniqueId(), GameUser.class);
        if (user == null) return false;

        return user.isInLobby();
    }
}
