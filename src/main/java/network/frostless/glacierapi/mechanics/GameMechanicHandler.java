package network.frostless.glacierapi.mechanics;

import network.frostless.glacierapi.user.GameUser;

public interface GameMechanicHandler<U extends GameUser> {

    void onDeath(U user);

    void spectate(U user);

    void respawn(U user);
}
