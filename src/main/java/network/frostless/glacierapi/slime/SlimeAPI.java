package network.frostless.glacierapi.slime;

import com.grinderwolf.swm.api.world.SlimeWorld;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface SlimeAPI {

    /**
     * Loads the specified world.
     * @param mapName The name of the map to load.
     * @return A {@link CompletableFuture} that will be completed when the world is loaded.
     */
    CompletableFuture<SlimeWorld> loadMap(@NotNull String mapName);

    /**
     * Generates the specified world. This
     * needs to be called after loading the map
     * to generate the world.
     * @param world The world to generate.
     * @return A {@link CompletableFuture} that will be completed when the world is generated.
     * @implNote This method is run sync
     */
    CompletableFuture<SlimeWorld> generateMap(@NotNull SlimeWorld world);

    /**
     * Clones the specified world and returns a new slime world
     * with the specified world name.
     * @param templateWorld The world to clone.
     * @param nameOfWorld The name of the new world.
     * @return A {@link CompletableFuture} that will be completed when the world is cloned.
     */
    CompletableFuture<SlimeWorld> generateAsTemplate(@NotNull SlimeWorld templateWorld, @NotNull String nameOfWorld);


    ExecutorService getExecutor();

}
