package network.frostless.glacier.slime;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import lombok.Getter;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.slime.SlimeAPI;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WorldManager implements SlimeAPI {

    @Getter
    private final ExecutorService executor = Executors.newCachedThreadPool(r -> new Thread(r, "Glacier World Manager"));

    private final SlimePlugin slime;
    private final SlimeLoader loader;

    public WorldManager(SlimePlugin slime) {
        this.slime = slime;
        this.loader = slime.getLoader("file");
    }


    @Override
    public CompletableFuture<SlimeWorld> loadMap(@NotNull String mapName) {
        final CompletableFuture<SlimeWorld> future = new CompletableFuture<>();

        slime.asyncLoadWorld(loader, mapName, true, getProperties()).whenComplete((world, err) -> {
            if (err != null) {
                future.completeExceptionally(err);
                return;
            }

            world.ifPresentOrElse(future::complete, () -> future.completeExceptionally(new Exception("Could not find world " + mapName)));
        });

        return future;
    }

    @Override
    public CompletableFuture<SlimeWorld> generateMap(@NotNull SlimeWorld world) {
        final CompletableFuture<SlimeWorld> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTask(Glacier.getPlugin(), () -> {
            slime.generateWorld(world);
            future.complete(world);
        });

        return future;
    }

    @Override
    public CompletableFuture<SlimeWorld> generateAsTemplate(@NotNull SlimeWorld templateWorld, @NotNull String nameOfWorld) {
        final CompletableFuture<SlimeWorld> future = new CompletableFuture<>();

        SlimeWorld clone = templateWorld.clone(nameOfWorld);
        generateMap(clone).whenComplete((cloned, err) -> future.complete(cloned));

        return future;
    }


    private SlimePropertyMap getProperties() {
        // fetch from database of sorts.
        SlimePropertyMap properties = new SlimePropertyMap();

        properties.setValue(SlimeProperties.DIFFICULTY, "normal");
        properties.setValue(SlimeProperties.SPAWN_X, 0);
        properties.setValue(SlimeProperties.SPAWN_Y, 0);
        properties.setValue(SlimeProperties.SPAWN_Z, 0);
        properties.setValue(SlimeProperties.ALLOW_ANIMALS, false);
        properties.setValue(SlimeProperties.ALLOW_MONSTERS, false);
        properties.setValue(SlimeProperties.DRAGON_BATTLE, false);

        return properties;
    }
}
