package network.frostless.glacier.countdown;

import com.google.common.collect.Maps;
import lombok.Setter;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacier.utils.GlobalTimer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CountdownManager {

    private GlobalTimer timer;

    private final Map<UUID, GameCountdown<?,?>> countdowns = Maps.newConcurrentMap();
    @Setter
    private Map<UUID, BukkitRunnable> bukkitRunnables = Maps.newConcurrentMap();


    public CountdownManager(GameCountdown<?, ?> ...countdown) {
        addCountdown(countdown);
    }

    public synchronized void addCountdown(GameCountdown<?, ?> ...countdown) {
        for (GameCountdown<?, ?> gameCountdown : countdown) {
            countdowns.put(UUID.randomUUID(), gameCountdown);
        }
    }

    public void startCountdowns() {
        timer = new GlobalTimer(1, 1, TimeUnit.SECONDS, this::check);
        timer.start();
    }

    public void check() {
        for (Map.Entry<UUID, GameCountdown<?, ?>> countdown : countdowns.entrySet()) {
            if(!preconditions(countdown.getValue())) {
                cancelRunnable(countdown.getKey());
                continue;
            }

            if(bukkitRunnables.get(countdown.getKey()) == null) {
                BukkitRunnable runnable = generateRunnable(countdown.getKey());
                bukkitRunnables.put(countdown.getKey(), runnable);
                runnable.runTaskTimer(Glacier.getPlugin(), 1, 20);
            }
        }

    }

    private void cancelRunnable(UUID uuid) {
        BukkitRunnable runnable = bukkitRunnables.get(uuid);
        if(runnable != null) {
            runnable.cancel();
            OffloadTask.offloadSync(() -> countdowns.get(uuid).onCancel());
            bukkitRunnables.remove(uuid);
        }
    }
    private BukkitRunnable generateRunnable(UUID countdownUUID) {
        GameCountdown<?, ?> countdown = countdowns.get(countdownUUID);
        return new BukkitRunnable() {
            int secondsPassed = 0;
            @Override
            public void run() {
                if(secondsPassed >= countdown.getTimer()) {
                    countdown.start();
                    cancel();
                    return;
                }
                countdown.onCount(secondsPassed);

                secondsPassed++;
            }
        };
    }

    public void pauseTimer() {
        timer.terminate();
    }


    public boolean preconditions(GameCountdown<?, ?> countdown) {
        return countdown.getGame().getPlayers().size() >= countdown.getMinPlayers();
    }
}
