package network.frostless.glacierapi.mechanics;

import network.frostless.glacier.team.Team;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameMechanic<G extends Game<U, Team<U>>, U extends GameUser> implements Listener {


    protected U getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    @SuppressWarnings("unchecked")
    protected U getUser(UUID uuid) {
        return (U) Users.getUser(uuid, GameUser.class);
    }

    @SuppressWarnings("unchecked")
    public Map<Class<? extends Event>, Method> getBukkitEvents() {
        Map<Class<? extends Event>, Method> methods = new HashMap<>();

        for (Method method : getClass().getMethods()) {
            if (method.getAnnotation(EventHandler.class) != null) {
                Class<?> paramType = method.getParameters()[0].getType();
                if (Event.class.isAssignableFrom(paramType)) {
                    methods.put((Class<? extends Event>) paramType, method);
                }
            }
        }
        return methods;
    }
}
