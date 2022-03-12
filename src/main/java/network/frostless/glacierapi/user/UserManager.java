package network.frostless.glacierapi.user;

import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacierapi.user.loader.UserLoaderResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface UserManager {

    <T> T getOrCreate(UUID uuid);

    CompletableFuture<UserLoaderResult> loadUser(GlobalUser user);
}
