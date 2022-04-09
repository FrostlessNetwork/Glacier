package network.frostless.glacierapi.user;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.rank.RankManager;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.UserGameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public interface GameUser extends DataGameUser {

    MiniMessage minimessage = MiniMessage.miniMessage();

    String getGameIdentifier();
    void setGameIdentifier(String identifier);

    GlobalUser getGlobalUser();
    void setGlobalUser(GlobalUser globalUser);

    UserGameState getUserState();
    void setUserState(UserGameState state);

    String getRank();
    void setRank(String rank);

    default Component getRankDisplay() {
        return minimessage.deserialize(RankManager.getRank(getRank()));
    }

    default <U extends GameUser, T extends Team<U>> Game<U, T> getGame() {
        return Glacier.get().getGameManager().getGame(getGameIdentifier());
    }

    default Player getPlayer() {
        return Bukkit.getPlayer(this.getUuid());
    }

    default boolean isInLobby() {
        return getUserState() == UserGameState.LOBBY;
    }

    void onStartGame();
}