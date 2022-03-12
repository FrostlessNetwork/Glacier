package network.frostless.glacier.user;

import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.GameUser;

import java.util.UUID;

public class Users {

    public static <T extends GameUser> T getUser(UUID uuid, Class<T> clazz) {
        return clazz.cast(Glacier.get().getUserManager().getOrCreate(uuid));
    }
}
