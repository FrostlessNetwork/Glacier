package network.frostless.glacierapi.game;

import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.data.GameState;
import network.frostless.glacierapi.user.GameUser;

public interface Game<U extends GameUser, T extends Team<U>> extends Minigame {

    /* Game state */
    GameState getGameState();
    void setGameState(GameState state);

    long getStartTime();
    void setStartTime(long time);
}
