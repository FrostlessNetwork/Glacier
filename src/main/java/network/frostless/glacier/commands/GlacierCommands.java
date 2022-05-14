package network.frostless.glacier.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import network.frostless.glacier.Glacier;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.game.manager.GameManager;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Map;

@Command("glacier")
public class GlacierCommands {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Subcommand("currentgames")
    @CommandPermission("glacier.currentgames")
    public void getCurrentGames(final Player player) {
        final GameManager gameManager = Glacier.get().getGameManager();

        Component header = miniMessage.deserialize("<gray>Current Games:");
        TagResolver tags = TagResolver.builder()
                .tag("games", Tag.inserting(Component.text(gameManager.getGames().size())))
                .build();
        Component body = miniMessage.deserialize("<gray>There are currently <yellow><games><gray> games running.", tags);

        Component gamesRunning = Component.empty();

        for (Map.Entry<String, Game<?,?>> entr : gameManager.getGames().entrySet()) {
            String gameName = entr.getKey();
            Game<?, ?> game = entr.getValue();
            TagResolver gameTags = TagResolver.builder()
                    .tag("game", Tag.inserting(Component.text(gameName)))
                    .tag("gametype", Tag.inserting(Component.text(game.getClass().getSimpleName())))
                    .tag("players", Tag.inserting(Component.text(gameManager.getGame(gameName).getPlayers().size())))
                    .tag("maxplayers", Tag.inserting(Component.text(gameManager.getGame(gameName).getMaxPlayers())))
                    .build();
            Component gameComponent = miniMessage.deserialize(
                    "<gray>- <yellow><game> <aqua>(<gametype>) <gray><players>/<maxplayers>",
                    gameTags
            );
            gameComponent = gameComponent.hoverEvent(Component.text("Click to view more info", NamedTextColor.AQUA));
            gameComponent = gameComponent.clickEvent(ClickEvent.runCommand("/glacier info " + gameName));
            gamesRunning = gamesRunning.append(gameComponent).append(Component.newline());
        }

        player.sendMessage(header.append(Component.newline()).append(body).append(Component.newline()).append(gamesRunning));
    }

    @Subcommand("info")
    @CommandPermission("glacier.info")
    public void getGameInfo(Player player, Game<?, ?> game) {
        TagResolver tags = TagResolver.builder()
                .tag("game", Tag.inserting(Component.text(game.getIdentifier())))
                .tag("gametype", Tag.inserting(Component.text(game.getClass().getSimpleName())))
                .tag("players", Tag.inserting(Component.text(game.getPlayers().size())))
                .tag("maxplayers", Tag.inserting(Component.text(game.getMaxPlayers())))
                .build();


        Component header = miniMessage.deserialize("<gray>Game <yellow><game> <gray>info:", tags);
        TextComponent body = Component
                .text("Game type: ", NamedTextColor.GRAY)
                .append(Component.text(game.getClass().getSimpleName(), NamedTextColor.YELLOW))
                .append(miniMessage.deserialize("<gray>Players: <aqua><players>/<maxplayers>", tags).hoverEvent(getPlayers(game)));
        player.sendMessage(header.append(Component.newline()).append(body));
    }

    @Subcommand("game forcestart")
    @CommandPermission("glacier.game.forcestart")
    public void forceStart(CommandSender sender, Game<?, ?> game) {
        try {
            game.forceStart();
            sender.sendMessage(miniMessage.deserialize("<gray>You have forced the game to start."));
        } catch (Exception e) {
            sender.sendMessage(miniMessage.deserialize("<gray>Failed to force the game to start. Check the console for more info."));
            e.printStackTrace();
        }
    }

    private Component getPlayers(Game<?, ?> game) {
        Component players = Component.empty();

        for (GameUser player : game.getPlayers()) {
            players = players.append(player.getDisplayName()).append(Component.newline());
        }

        return players;
    }
}
