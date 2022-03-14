package network.frostless.glacier.game;

import com.google.common.collect.Maps;
import lombok.Getter;
import network.frostless.glacierapi.game.manager.GameManager;

import java.util.Map;

@Getter
public class GameManagerImpl implements GameManager {

    private final Map<String, Object> games = Maps.newConcurrentMap();


    public GameManagerImpl() {}


}
