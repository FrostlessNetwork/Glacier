package network.frostless.glacierapi.user;

import network.frostless.frostentities.entity.GlobalUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface GameUser extends DataGameUser {


    String getGameIdentifier();
    void setGameIdentifier(String identifier);

    GlobalUser getGlobalUser();
    void setGlobalUser(GlobalUser globalUser);


    default Player getPlayer() {
        return Bukkit.getPlayer(this.getUuid());
    }
}
