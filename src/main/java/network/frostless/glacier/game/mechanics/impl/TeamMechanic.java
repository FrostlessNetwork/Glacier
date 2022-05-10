package network.frostless.glacier.game.mechanics.impl;

import network.frostless.glacier.Glacier;
import network.frostless.glacier.async.OffloadTask;
import network.frostless.glacierapi.events.game.GameUserJoinTeamEvent;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacierapi.mechanics.Mechanic;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.event.EventHandler;

public class TeamMechanic<U extends GameUser> extends Mechanic<U> {

    public TeamMechanic(GameMechanicHandler<U> handler) {
        super(handler);
    }

    @EventHandler
    public void onJoinTeam(GameUserJoinTeamEvent evt) {
        org.bukkit.scoreboard.Team team1 = Glacier.get().getGameBoard().getTeam(evt.getGameUser().getGame(), evt.getTeam());
        OffloadTask.offloadSync(() -> team1.addPlayer(evt.getGameUser().getPlayer()));
    }
}
