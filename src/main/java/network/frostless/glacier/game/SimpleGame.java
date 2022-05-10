package network.frostless.glacier.game;

import com.google.common.base.Objects;
import lombok.Data;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.countdown.CountdownManager;
import network.frostless.glacier.countdown.GameCountdown;
import network.frostless.glacier.countdown.impl.GameStartCountdown;
import network.frostless.glacier.game.events.DefaultGameEventManager;
import network.frostless.glacierapi.game.event.GameEvent;
import network.frostless.glacier.game.mechanics.DefaultGameMechanicHandler;
import network.frostless.glacierapi.game.event.GameEventManager;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.map.MapMeta;
import network.frostless.glacierapi.user.GameUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Data
public abstract class SimpleGame<U extends GameUser, T extends Team<U>> implements Game<U, T> {
    protected Logger logger = LogManager.getLogger("Game " + getIdentifier());

    private GameState gameState;

    private int maxPlayers;

    private GameMechanicHandler<U> mechanicHandler = new DefaultGameMechanicHandler<>();

    private String identifier;

    private World world;
    private MapMeta mapMeta;

    private long startTime;

    private List<T> teams = new ArrayList<>();
    private List<U> players = new ArrayList<>();

    private int minPlayers;

    private List<U> spectators = new ArrayList<>();

    private GameEventManager<Game<U, T>> eventManager = new DefaultGameEventManager<>(this);

    public SimpleGame(int minPlayers, int maxPlayers) {
        gameState = GameState.WAITING;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public void onReady() {
        init();
    }


    protected void init() {
        addCountdown(new GameStartCountdown<>(60, 1, this));
    }

    protected void addCountdown(GameCountdown<U, T> countdown) {
        Glacier.get().getCountdownManager().addCountdown(countdown);
    }

    public void forceStart() {
        // TODO: fix this later kthx countdown should have a flag to force start not just literally force it to
        CountdownManager cdm = Glacier.get().getCountdownManager();
        List<GameCountdown<?, ?>> cds = cdm.getCountdowns(this);
        if (cds.size() > 0) {
            GameCountdown<?, ?> gcd = cds.get(0);
            gcd.start();
            cdm.stopCountdown(gcd);
        } else start();
    }

    @SuppressWarnings("unchecked")
    public void addGameEvent(GameEvent<? extends Game<U, T>> event) {
        eventManager.addEvent((GameEvent<Game<U, T>>) event);
    }

    @SuppressWarnings("unchecked")
    public void addGameEvent(GameEvent<? extends Game<U, T>>... events) {
        for (GameEvent<? extends Game<U, T>> event : events) {
            eventManager.addEvent((GameEvent<Game<U, T>>) event);
        }
    }

    public abstract void applyMapMapper(MapMeta mapMeta);

    @Override
    public Team<U> getTeamForPlayer(U player) {
        for (T team : teams) {
            if (team.getPlayers().contains(player)) return team;
        }
        return null;
    }

    @Override
    public void addPlayer(U user) {
        players.add(user);
    }

    @Override
    public void removePlayer(U user) {
        players.remove(user);
    }

    @Override
    public int getIngamePlayers() {
        return (int) players.stream().filter(u -> !spectators.contains(u)).count();
    }

    @Override
    public Location getWorldCenter() {
        return world.getSpawnLocation();
    }

    @Override
    public int getSpectatingPlayers() {
        return spectators.size();
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
        logger = LogManager.getLogger("Game " + identifier); // fix logger displaying null...
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleGame<?, ?> that)) return false;

        return Objects.equal(getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdentifier());
    }
}
