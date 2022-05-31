package network.frostless.glacier.countdown.impl;

import com.grinderwolf.swm.api.world.SlimeWorld;
import net.kyori.adventure.audience.MessageType;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacier.countdown.GameCountdown;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.game.data.UserGameState;
import network.frostless.glacierapi.map.MapMeta;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.Sound;

import java.util.concurrent.CompletableFuture;

/**
 * An implementation of {@link GameCountdown} which allows for default
 * map loading when there are enough users.
 *
 * @param <U> The type of {@link GameUser}
 * @param <T> The type of {@link Team}
 */
public abstract class GameStartCountdown<U extends GameUser, T extends Team<U>> extends GameCountdown<U, T> {

    /**
     * Creates a new game countdown.
     *
     * @param timer      The amount of time in seconds that the countdown will last.
     * @param minPlayers The minimum amount of players that must be in the game to start the countdown.
     * @param game       The game that the countdown is for.
     */
    public GameStartCountdown(int timer, int minPlayers, Game<U, T> game) {
        super(timer, minPlayers, game);
    }

    @Override
    public void start() {
        final String map = "TheBridge_Castle_NGX_VM";
        CompletableFuture<SlimeWorld> loadGameMap = Glacier.get().getWorldManager().loadMap(map);
        loadGameMap.whenComplete((world, throwable) -> {
            if (throwable != null) {
                getGame().executePlayers(p -> p.sendMessage(Glacier.miniMessage.deserialize("<red>Failed to load map."), MessageType.SYSTEM));
                throwable.printStackTrace();
                return;
            }

            CompletableFuture<SlimeWorld> templateMap = Glacier.get().getWorldManager().generateAsTemplate(world, getGame().getIdentifier());

            templateMap.whenComplete((template, throwable1) -> {
                if (throwable1 != null) {
                    getGame().executePlayers(p -> p.sendMessage(Glacier.miniMessage.deserialize("<red>Failed to generate template."), MessageType.SYSTEM));
                    throwable1.printStackTrace();
                    return;
                }
                System.out.println("Template for " + map + " generated with name " + template.getName());
                OffloadTask.offloadSync(() -> {
                    MapMeta meta = Glacier.get().getMapManager().getMap(map);
                    getGame().setMapMeta(meta);
                    getGame().setWorld(Bukkit.getWorld(template.getName()));
                    getGame().applyMapMapper(meta);
                });
                onGeneratedWorld(template);
            });
        });
    }


    protected abstract void onGeneratedWorld(SlimeWorld world);


    @Override
    public void onEnoughPlayers() {
        getGame().broadcast("<green>Starting game in <yellow>" + getTimer() + "<green> seconds.");
        getGame().executePlayers(p -> p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1));
    }

    @Override
    public void onCancel() {
        getGame().executePlayers(p -> p.sendMessage(Glacier.miniMessage.deserialize("<yellow>Game start cancelled."), MessageType.SYSTEM));
    }

    @Override
    public void onCount(int curr) {
        getGame().executePlayers(p -> p.setLevel(getTimer() - curr));
        if (curr % 10 == 0 || curr >= getTimer() - 10) {
            getGame().executeUsers(p -> {
                int time = getTimer() - curr;
                p.getPlayer().playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
                p.sendMessage("<green>Game is starting in <aqua>" + time + " <green>seconds!", MessageType.SYSTEM);
            });
        }
    }
}
