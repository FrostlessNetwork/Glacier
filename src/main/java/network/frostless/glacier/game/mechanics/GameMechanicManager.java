package network.frostless.glacier.game.mechanics;

import network.frostless.glacier.Glacier;
import network.frostless.glacier.team.Team;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.mechanics.GameMechanic;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * this is so fucking dog shit please fix it....
 *
 * @param <G>
 * @param <U>
 */
public class GameMechanicManager<G extends Game<U, Team<U>>, U extends GameUser> {


    private final Map<Class<? extends GameMechanic<G, U>>, ? super GameMechanic<G, U>> mechanics = new HashMap<>();
    private static final Map<Class<? extends Event>, List<Method>> events = new HashMap<>();


    public GameMechanicManager() {
    }

    @SuppressWarnings("unchecked")
    public <V extends GameMechanic<G, U>> V get(Class<V> mechanic) {
        return (V) mechanics.get(mechanic);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final void add(GameMechanic<G, U>... mechanics) {
        for (GameMechanic<G, U> mechanic : mechanics) {
            this.mechanics.put((Class<? extends GameMechanic<G, U>>) mechanic.getClass(), mechanic);
            Glacier.getPlugin().registerListeners(mechanic);
        }
    }

//    private static void registerListeners(GameMechanic<?, ?>... mechanics) {
//        for (GameMechanic<?, ?> mechanic : mechanics) {
//            for (Map.Entry<Class<? extends Event>, Method> entr : mechanic.getBukkitEvents().entrySet()) {
//                events.merge(entr.getKey(), List.of(entr.getValue()), (prev, next) -> {
//                    prev.addAll(next);
//                    return prev;
//                });
//            }
//        }
//
//        for (Map.Entry<Class<? extends Event>, List<Method>> entry : events.entrySet()) {
//            Glacier.server().getPluginManager().registerEvent(entry.getKey(), new EmptyListener(), EventPriority.NORMAL, (listener, evtx) -> {
//                if (!events.containsKey(evtx.getClass())) return;
//
//                for (Method method : events.get(evtx.getClass())) {
//                    try {
//                        method.invoke(listener, evtx);
//                    } catch (InvocationTargetException | IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, Glacier.getPlugin());
//        }
//    }

    private static Optional<Game<?, ?>> getGame(World world) {
        return Glacier.get().getGameManager().getGames().values().stream().filter(c -> c.getWorld() != null && c.getWorld().equals(world)).findFirst();
    }

    private static final class EmptyListener implements Listener {
    }
}
