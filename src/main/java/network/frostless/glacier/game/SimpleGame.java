package network.frostless.glacier.game;

import com.google.common.base.Objects;
import lombok.Data;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.countdown.GameCountdown;
import network.frostless.glacier.countdown.impl.GameStartCountdown;
import network.frostless.glacier.game.mechanics.DefaultGameMechanicHandler;
import network.frostless.glacier.game.mechanics.impl.DeathMechanic;
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

import java.util.ArrayList;
import java.util.List;

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

    public SimpleGame(int minPlayers, int maxPlayers) {
        gameState = GameState.WAITING;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;

        init();
    }


    protected void init() {
        addCountdown(new GameStartCountdown<>(60, 1, this));
    }

    protected void addCountdown(GameCountdown<U, T> countdown) {
        Glacier.get().getCountdownManager().addCountdown(countdown);
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
        System.out.println("CALLED EQUALS");
        System.out.println("ARE WE EQUAL??? " + Objects.equal(getIdentifier(), that.getIdentifier()));
        return Objects.equal(getIdentifier(), that.getIdentifier());
    }

    @Override
    public int hashCode() {
        System.out.println("CALLED HASHCODE");
        System.out.println("ARE WE HASHED????? " + Objects.hashCode(getIdentifier()));
        return Objects.hashCode(getIdentifier());
    }
}
