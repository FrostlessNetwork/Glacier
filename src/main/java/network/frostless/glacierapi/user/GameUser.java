package network.frostless.glacierapi.user;

import network.frostless.frostentities.entity.GlobalUser;

public interface GameUser {

    long getId();
    void setId(long id);

    String getGameIdentifier();
    void setGameIdentifier(String identifier);

    GlobalUser getGlobalUser();
    void setGlobalUser(GlobalUser globalUser);
}
