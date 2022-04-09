package network.frostless.glacier.async;

import network.frostless.glacier.Glacier;
import org.bukkit.Bukkit;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class OffloadTask {
    public static void offloadAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Glacier.getPlugin(), runnable);
    }

    public static <T> CompletableFuture<T> offload(Consumer<CompletableFuture<T>> consumer) {
        CompletableFuture<T> future = new CompletableFuture<>();

        consumer.accept(future);

        return future;
    }

    public static void offloadSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(Glacier.getPlugin(), runnable);
    }

    public static void offloadDelayedSync(Runnable runnable, int delay) {
        Bukkit.getScheduler().runTaskLater(Glacier.getPlugin(), runnable, delay);
    }

}
