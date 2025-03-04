package network.frostless.glacier.team;

import com.google.common.collect.Lists;
import lombok.Data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.events.game.GameUserJoinTeamEvent;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@Data
public abstract class Team<P extends GameUser> {

    /**
     * The UUID of the team. This should
     * be unique.
     */
    private UUID uuid;

    private List<P> players;
    private int size;

    // Team meta
    private TeamColor teamColor = TeamColor.WHITE;
    @Nullable
    private String name;


    public Team(int size) {
        this.size = size;
        this.uuid = UUID.randomUUID();
        this.players = Lists.newArrayListWithCapacity(size);
    }

    public void addPlayer(P player) {
        if (!players.contains(player))
            players.add(player);

        //    Glacier.callEvent(new GameUserJoinTeamEvent(player, this));
    }

    public void removePlayer(P player) {
        players.remove(player);
    }

    public boolean isPartOfTeam(P player) {
        return players.contains(player);
    }

    public boolean isPartOfTeam(Player player) {
        return players.stream().map(GameUser::getUuid).anyMatch(player.getUniqueId()::equals);
    }

    public boolean isFull() {
        return players.size() >= size;
    }

    public String getDisplayName() {
        return name != null ? teamColor.getChatColor() + name : teamColor.getChatColor() + teamColor.getName();
    }

    public Component displayName() {
        return Component.text( teamColor.getName(), teamColor.getNamedTextColor()).decorate(TextDecoration.BOLD);
    }
}
