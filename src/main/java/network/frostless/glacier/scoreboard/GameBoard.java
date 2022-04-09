package network.frostless.glacier.scoreboard;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.scoreboard.core.FrostBoard;
import network.frostless.glacierapi.game.Game;
import network.frostless.glacierapi.user.GameUser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class GameBoard extends FrostBoard {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected MiniMessage mm = MiniMessage.miniMessage();

    @Getter
    protected final GameUser user;

    protected final Game<?, ?> game;


    public GameBoard(GameUser gameUser, String objectiveName) {
        super(gameUser.getPlayer(), objectiveName);
        this.user = gameUser;
        this.game = gameUser.getGame();
    }

    protected String[] formatPC(String ...text) {
        List<String> formatted = new ArrayList<>();

        TagResolver.Single[] placeholders = new TagResolver.Single[] {
                Placeholder.component("dtf", Component.text(dtf.format(LocalDateTime.now()))),
                Placeholder.component("player", Component.text(getPlayer().getName())),
        };

        for (String s : text) {
            Component comp = mm.deserialize(s, placeholders);

            formatted.add(LegacyComponentSerializer.legacySection().serialize(comp));
        }

        return formatted.toArray(new String[0]);
    }
}
