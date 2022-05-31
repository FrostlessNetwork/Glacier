package network.frostless.glacierapi.user.loader;

import network.frostless.glacierapi.user.GameUser;

import java.util.UUID;

public interface UserDataLoader<T extends GameUser> {

    T createUser(String username, UUID uuid);

    Class<T> getUserClass();
}
