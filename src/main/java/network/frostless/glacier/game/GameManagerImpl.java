package network.frostless.glacier.game;

import com.google.common.collect.Maps;
import lombok.Getter;
import network.frostless.frostcore.messaging.redis.Redis;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.team.Team;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.manager.GameManager;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class GameManagerImpl implements GameManager {

    private final Map<String, Object> games = Maps.newConcurrentMap();
    private final Redis<String, String> redis;


    public GameManagerImpl() {
        this.redis = Glacier.get().getFrostbite().getRedis();
    }


    @Override
    public <U extends GameUser, T extends Team<U>> CompletableFuture<String> createGame(Game<U, T> game) {

        String identifier = "G-" + ThreadLocalRandom.current().nextInt(100, 300);

        while (this.games.containsKey(identifier)) {
            identifier = "G-" + ThreadLocalRandom.current().nextInt(100, 300);
        }

        games.put(identifier, game);

        game.setIdentifier(identifier);


        Glacier.getLogger().info("Created game: " + identifier);

        return CompletableFuture.completedFuture(identifier);
    }

    @Override
    public List<GameUser> usersIn(String identifier) {
        return Glacier.get().getUserManager().getUsers().stream().filter(user -> user.getGameIdentifier().equals(identifier)).collect(Collectors.toList());
    }


    @Override
    public synchronized void adjustVisibility(GameUser origin) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            GameUser user = Users.getUser(onlinePlayer.getUniqueId(), GameUser.class);

            if(user.getGameIdentifier().equals(origin.getGameIdentifier())) {
                onlinePlayer.showPlayer(Glacier.getPlugin(), origin.getPlayer());
                origin.getPlayer().showPlayer(Glacier.getPlugin(), onlinePlayer);
            } else {
                onlinePlayer.hidePlayer(Glacier.getPlugin(), origin.getPlayer());
                origin.getPlayer().hidePlayer(Glacier.getPlugin(), onlinePlayer);
            }

        }
    }

    @Override
    public boolean hasGame(String identifier) {
        return games.containsKey(identifier);
    }
}
