package network.frostless.glacierapi.mechanics;

import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.Listener;

import java.util.UUID;

public abstract class Mechanic<U extends GameUser> implements Listener {

    protected GameMechanicHandler<U> mechanicHandler;

    public Mechanic(GameMechanicHandler<U> handler) {
        mechanicHandler = handler;
    }

    @SuppressWarnings("unchecked")
    protected U getUser(UUID uuid) {
        return (U) Users.getUser(uuid, GameUser.class);
    }
}