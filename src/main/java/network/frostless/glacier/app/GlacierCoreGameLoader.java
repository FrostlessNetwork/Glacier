package network.frostless.glacier.app;

import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.loader.UserDataLoader;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GlacierCoreGameLoader<User extends GameUser> extends JavaPlugin implements UserDataLoader<User> {

    protected void initGlacier() {
        Glacier<User> glacierAPI = Glacier.get(getUserClass());
        glacierAPI.setPlugin(this);
        glacierAPI.setUserDataLoader(this);
    }



    @Override
    public void onEnable() {
        super.onEnable();
        initGlacier();
        init();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        shutdown();
    }


    public abstract void init();


    public abstract void shutdown();
}
