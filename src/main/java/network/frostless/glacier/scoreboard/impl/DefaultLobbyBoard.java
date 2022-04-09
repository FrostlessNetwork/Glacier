package network.frostless.glacier.scoreboard.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import network.frostless.glacier.scoreboard.GameBoard;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;

public class DefaultLobbyBoard extends GameBoard {

    private final GameUser gameUser;

    public DefaultLobbyBoard(GameUser user) {
        super(user, LegacyComponentSerializer.legacySection().serialize(Component.text("Glacier Lobby").color(TextColor.color(0x00FF00))));
        this.gameUser = user;
    }

    @Override
    public void updateBoard() {
        updateLines("GAME SERVER " + gameUser.getGameIdentifier());
    }
}
