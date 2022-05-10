package network.frostless.glacierapi.game.event;

import network.frostless.glacier.team.Team;
import network.frostless.glacier.utils.Pair;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.user.GameUser;

import java.util.concurrent.TimeUnit;

public interface GameEvent<G extends Game<? extends GameUser, ? extends Team<? extends GameUser>>> {
    void execute(G game);


    Pair<Integer, TimeUnit> getDelay();


    String getName();
}
