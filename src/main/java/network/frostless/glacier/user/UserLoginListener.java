package network.frostless.glacier.user;

import net.kyori.adventure.text.Component;
import network.frostless.bukkitapi.events.UserJoinEvent;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.loader.UserLoaderResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.concurrent.CompletableFuture;

public class UserLoginListener implements Listener {

    private final Logger logger = LogManager.getLogger();

    @EventHandler
    public void onLogin(UserJoinEvent event) {
        CompletableFuture<UserLoaderResult> future = Glacier.get().getUserManager().loadUser(event.getUser());

        future.whenComplete((res, err) -> {
            switch (res) {
                case DENIED -> event.getPlayer().kick(Component.text("You are not allowed to join this server."), PlayerKickEvent.Cause.ILLEGAL_ACTION);
                case ALLOWED -> logger.info("User " + event.getPlayer().getName() + " has been authenticated through the Glacier API.");
            }
        });
    }
}