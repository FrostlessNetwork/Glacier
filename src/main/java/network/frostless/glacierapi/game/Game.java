package network.frostless.glacierapi.game;

import network.frostless.glacierapi.game.data.GameState;

public interface Game extends Minigame {

    /* Game state */
    GameState getGameState();
    void setGameState(GameState state);


}
