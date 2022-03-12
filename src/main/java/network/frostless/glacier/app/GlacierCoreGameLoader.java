package network.frostless.glacier.app;

import network.frostless.bukkitapi.SpigotCoreLoader;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.loader.UserDataLoader;

public abstract class GlacierCoreGameLoader<User extends GameUser> extends SpigotCoreLoader implements UserDataLoader<User> {

    protected void initGlacier() {
        Glacier.setPlugin(this);
        //
        Glacier<User> glacierAPI = Glacier.get(getUserClass());
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
