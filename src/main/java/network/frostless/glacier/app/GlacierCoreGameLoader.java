package network.frostless.glacier.app;

import network.frostless.bukkitapi.SpigotCoreLoader;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.loader.UserDataLoader;

public abstract class GlacierCoreGameLoader<User extends GameUser, Team extends network.frostless.glacier.team.Team<User>> extends SpigotCoreLoader implements UserDataLoader<User> {


    protected Glacier<User, Team> glacierAPI;

    @SuppressWarnings("unchecked")
    protected void initGlacier() {
        Glacier.setPlugin(this);

        refreshConfigurations();

        glacierAPI = (Glacier<User, Team>) Glacier.get();
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
