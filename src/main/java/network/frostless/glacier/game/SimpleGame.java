package network.frostless.glacier.game;

import lombok.Data;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.countdown.GameCountdown;
import network.frostless.glacier.countdown.impl.GameStartCountdown;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.map.MapMeta;
import network.frostless.glacierapi.user.GameUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class SimpleGame<U extends GameUser, T extends Team<U>> implements Game<U, T> {

    protected Logger logger = LogManager.getLogger("Game " + getIdentifier());

    private GameState gameState;

    private int maxPlayers;

    private String identifier;

    private World world;
    private MapMeta mapMeta;

    private long startTime;
    private List<U> players = new ArrayList<>();

    private List<GameCountdown<U, T>> countdowns = new ArrayList<>();

    public SimpleGame() {
        gameState = GameState.WAITING;

        init();

        Glacier.get().getCountdownManager().addCountdown(countdowns.toArray(GameCountdown[]::new));
    }


    protected void init() {
        addCountdown(new GameStartCountdown<>(60, 1, this));
    }

    protected void addCountdown(GameCountdown<U, T> countdown) {
        countdowns.add(countdown);
    }


    public abstract void applyMapMapper(MapMeta mapMeta);

    @Override
    public void addPlayer(U user) {
        players.add(user);
    }

    @Override
    public void removePlayer(U user) {
        players.remove(user);
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        logger = LogManager.getLogger("Game " + identifier); // fix logger displaying null...
    }
}
