package network.frostless.glacier.game.mechanics.impl;

import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacierapi.mechanics.Mechanic;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathMechanic<U extends GameUser> extends Mechanic<U> {

    public DeathMechanic(GameMechanicHandler<U> handler) {
        super(handler);
    }

    @EventHandler
    public void killOnVoid(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            player.setHealth(0.0D);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent evt) {
        if (Users.getUser(evt.getPlayer().getUniqueId(), GameUser.class).isInLobby()) return;
        OffloadTask.offloadDelayedSync(() -> mechanicHandler.onDeath(getUser(evt.getPlayer().getUniqueId())), 1);
    }
}
