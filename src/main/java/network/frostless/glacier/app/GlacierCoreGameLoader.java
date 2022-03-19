package network.frostless.glacier.app;

import io.lettuce.core.pubsub.RedisPubSubListener;
import lombok.Getter;
import network.frostless.bukkitapi.SpigotCoreLoader;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.identify.IdentifyListener;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.glacierapi.user.loader.UserDataLoader;
import network.frostless.serverapi.RedisKeys;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class GlacierCoreGameLoader<User extends GameUser> extends SpigotCoreLoader implements UserDataLoader<User> {


    protected Glacier<User> glacierAPI;

    @Getter
    @NotNull
    private final String serverIdentifier = Base64.getEncoder().encodeToString(Bukkit.getIp().getBytes(StandardCharsets.UTF_8));

    @Getter
    private String registeredIdentifier;

    protected void initGlacier() {
        Glacier.setPlugin(this);

        refreshConfigurations();

        glacierAPI = Glacier.get(getUserClass());

        glacierAPI.getFrostbite().getRedis().subscribe(RedisKeys.PS_SERVER_IDENTIFY(getServerIdentifier()));
        glacierAPI.getFrostbite().getRedis().addSubscribe(new IdentifyListener(this::onIdentify));
    }


    private void onIdentify(String identify) {
        Glacier.getLogger().info("Received server identifier: {}", identify);
        this.registeredIdentifier = identify;
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
