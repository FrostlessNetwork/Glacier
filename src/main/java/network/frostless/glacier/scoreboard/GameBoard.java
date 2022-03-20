package network.frostless.glacier.scoreboard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import network.frostless.glacier.scoreboard.core.FrostBoard;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public abstract class GameBoard extends FrostBoard {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected MiniMessage mm = MiniMessage.miniMessage();


    public GameBoard(Player player, String objectiveName) {
        super(player, objectiveName);
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
