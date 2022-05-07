package network.frostless.glacier.game;

import com.google.common.collect.Maps;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import network.frostless.glacier.team.Team;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import java.util.IdentityHashMap;
import java.util.Map;

@Getter
public class GameBoardManager<U extends GameUser, T extends Team<U>> {

    private final Map<Game<U, T>, Scoreboard> scoreboards = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    public void createBoardAnon(Game<?, ?> game) {
        createBoard((Game<U, T>) game);
    }

    public void createBoard(Game<U, T> game) {
        Scoreboard gameBoard = Bukkit.getScoreboardManager().getNewScoreboard();

        org.bukkit.scoreboard.Team spec_999 = gameBoard.registerNewTeam(String.format("%s-SPEC_999", game.getIdentifier()));
        spec_999.prefix(Component.text("[SPEC]", NamedTextColor.GRAY).append(Component.space()));
        spec_999.color(NamedTextColor.GRAY);

        spec_999.setCanSeeFriendlyInvisibles(true);
        spec_999.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        spec_999.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        spec_999.setOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);

        for (T team : game.getTeams()) {
            org.bukkit.scoreboard.Team team_ = gameBoard.registerNewTeam(String.format("%s-%s", game.getIdentifier(), team.getUuid()));
            team_.prefix(Component.text(team.getDisplayName()));
            team_.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.FOR_OTHER_TEAMS);
            team_.setOption(org.bukkit.scoreboard.Team.Option.NAME_TAG_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.ALWAYS);
            team_.setOption(org.bukkit.scoreboard.Team.Option.DEATH_MESSAGE_VISIBILITY, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        }
        scoreboards.put(game, gameBoard);
    }

    public org.bukkit.scoreboard.Team getSpectator(Game<U, T> game) {
        return scoreboards.get(game).getTeam(String.format("%s-SPEC_999", game.getIdentifier()));
    }

    public org.bukkit.scoreboard.Team getTeam(Game<U, T> game, T team) {
        return scoreboards.get(game).getTeam(String.format("%s-%s", game.getIdentifier(), team.getUuid()));
    }


    public void removeBoard(Game<U, T> game) {
        scoreboards.remove(game);
    }
}
