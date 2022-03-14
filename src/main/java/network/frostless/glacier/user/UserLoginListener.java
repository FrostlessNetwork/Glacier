package network.frostless.glacier.user;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import network.frostless.bukkitapi.events.AsyncUserLoginEvent;
import network.frostless.bukkitapi.events.UserLoginEvent;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.loader.UserLoaderResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.*;

public class UserLoginListener implements Listener {

    private final Logger logger = LogManager.getLogger();
    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> new Thread(r, "Glacier-User-Loader"));

    @EventHandler
    public void onAsyncLogin(AsyncUserLoginEvent event) {
        try {
            CompletableFuture<UserLoaderResult> future = Glacier.get().getUserManager().verifyUser(event.getUser());
            UserLoaderResult res = future.get(10, TimeUnit.SECONDS);

            switch (res) {
                case DENIED -> {
                    event.setComponent(Component.text("You are not allowed on this server!").color(TextColor.color(0xFF0000)));
                    event.setCancelled(true);
                }
                case ALLOWED -> logger.info("User " + event.getPlayerUUID() + " has been authenticated through the Glacier API.");
                default -> {
                    logger.info("Could not auth user.");
                    logger.warn("No authentication method was returned for user " + event.getPlayerUUID() + ".");
                    logger.error("User " + event.getPlayerUUID() + " has been denied access through Glacier.");
                    event.setCancelled(true);
                }
            }
        } catch (InterruptedException | ExecutionException | TimeoutException err) {
            logger.error("Error loading into Glacier for user: {} ", event.getUser().getUsername());
            event.setComponent(Component.text("Could not load your user data").color(TextColor.color(0xFF0000)));
            event.setCancelled(true);
            err.printStackTrace();
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(UserLoginEvent event) {
        Glacier.get().getUserManager().loadUser(event.getPlayer(), event.getUser());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().teleportAsync(new Location(Bukkit.getWorld("ptbl-slime"), 26, 12, -26)).whenComplete((v, err) -> {
            if (err != null) {
                logger.error("Error teleporting player to spawn: {}", err.getMessage());
            }
            if(v) {
                logger.info("Player " + event.getPlayer().getName() + " has been teleported to spawn.");
            } else {
                logger.warn("Player " + event.getPlayer().getName() + " could not be teleported to spawn.");
            }
        });
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onLogout(PlayerQuitEvent event) {
        executorService.submit(() -> Glacier.get().getUserManager().unloadUser(event.getPlayer()));
    }
}