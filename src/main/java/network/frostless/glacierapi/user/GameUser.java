package network.frostless.glacierapi.user;

import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import network.frostless.frostentities.entity.GlobalUser;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.rank.RankManager;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.data.UserGameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

// See F-bounded types https://stackoverflow.com/questions/2413829/java-interfaces-and-return-types
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

    default Location getLocation() {
        return getPlayer().getLocation();
    }

    default void sendMessage(Component component) {
        getPlayer().sendMessage(component);
    }

    default void sendMessage(String message) {
        getPlayer().sendMessage(Glacier.miniMessage.deserialize(message));
    }

    default void sendMessage(String message, MessageType type) {
        getPlayer().sendMessage(Glacier.miniMessage.deserialize(message), type);
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

    default boolean isSpectator() {
        return getUserState() == UserGameState.SPECTATING;
    }

    default Component getTeamDisplayName() {
        Team<GameUser> team = getGame().getTeamForPlayer(this);
        if (team == null) {
            return name();
        } else {
            return team.displayName().append(name().color(team.getTeamColor().getNamedTextColor()));
        }
    }

    default Component name() {
        return Component.text(getPlayer().getName());
    }


    default Component getDisplayName() {
        return getRankDisplay().append(Component.text(getPlayer().getName()));
    }

    void onStartGame();

    <U extends GameUser> boolean areTeamMates(U user);

    Location getSpawnpoint();

    void setSpawnpoint(Location location);
}