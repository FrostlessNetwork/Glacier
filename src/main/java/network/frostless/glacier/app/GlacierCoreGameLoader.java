package network.frostless.glacier.app;

import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.loader.UserDataLoader;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GlacierCoreGameLoader<User extends GameUser> extends JavaPlugin implements UserDataLoader<User> {

}
