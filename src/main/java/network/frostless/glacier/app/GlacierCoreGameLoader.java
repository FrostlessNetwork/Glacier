package network.frostless.glacier.app;

import network.frostless.bukkitapi.SpigotCoreLoader;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.loader.UserDataLoader;

public abstract class GlacierCoreGameLoader<User extends GameUser> extends SpigotCoreLoader implements UserDataLoader<User> {


    protected Glacier<User> glacierAPI;

    protected void initGlacier() {
        Glacier.setPlugin(this);

        refreshConfigurations();

        glacierAPI = Glacier.get(getUserClass());
    }


    public void refreshConfigurations() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onEnable() {
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
