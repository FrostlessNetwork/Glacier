package network.frostless.glacier.game;

import com.google.common.collect.Maps;
import lombok.Getter;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.manager.GameManager;
import network.frostless.glacierapi.user.GameUser;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;

@Getter
public class GameManagerImpl implements GameManager {

    private final Map<String, Object> games = Maps.newConcurrentMap();


    public GameManagerImpl() {}


    @Override
    public <U extends GameUser, T extends Team<U>> String createGame(Game<U, T> game) {

        return Strings.EMPTY;
    }
}
