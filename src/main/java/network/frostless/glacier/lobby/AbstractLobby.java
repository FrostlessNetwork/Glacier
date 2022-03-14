package network.frostless.glacier.lobby;

import network.frostless.glacier.Glacier;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.events.game.GameJoinEvent;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public abstract class AbstractLobby implements Listener, Lobby {

    public AbstractLobby() {
        Glacier.getPlugin().registerListeners(this);
    }

    @EventHandler
    public void onPlayerJoin(GameJoinEvent event) {

    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if(isLobbyUser(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(EntityDamageEvent evt) {
        if(evt.getEntity() instanceof Player player) {
            GameUser user = Users.getUser(player.getUniqueId(), GameUser.class);
            if(user != null && user.getUserState() != UserGameState.LOBBY) return;

            if(evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                //
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent evt) {
        if(evt.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if(isLobbyUser(evt.getPlayer())) evt.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {

        if(event instanceof Player player) {
            if(isLobbyUser(player)) event.setCancelled(true);
        }

    }

    private boolean isLobbyUser(Player player) {
        GameUser user = Users.getUser(player.getUniqueId(), GameUser.class);
        if(user == null) return false;


        return user.getUserState() == UserGameState.LOBBY;
    }
}
