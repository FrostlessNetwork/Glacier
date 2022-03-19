package network.frostless.glacier.game;

import com.google.common.collect.Maps;
import lombok.Getter;
import network.frostless.frostcore.messaging.redis.Redis;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.manager.GameManager;
import network.frostless.glacierapi.user.GameUser;
import network.frostless.serverapi.RedisKeys;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Getter
public class GameManagerImpl implements GameManager {

    private final Map<String, Object> games = Maps.newConcurrentMap();
    private final Redis<String, String> redis;


    public GameManagerImpl() {
        this.redis = Glacier.get().getFrostbite().getRedis();
    }


    @Override
    public <U extends GameUser, T extends Team<U>> CompletableFuture<String> createGame(Game<U, T> game) {
        Map<String, String> gamesInServer = redis.async().hgetall(RedisKeys.H_GAME_ALL("TEST")).toCompletableFuture().join();


        return CompletableFuture.completedFuture(Strings.EMPTY);
    }
}
