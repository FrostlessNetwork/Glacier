package network.frostless.glacier.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import network.frostless.bukkitapi.ViewerFilter;
import org.bukkit.entity.Player;

import java.util.Set;

public abstract class AbstractChat implements ChatRenderer, ViewerFilter {

    @Override
    public Set<Audience> filterViewers(Player source, Set<Audience> viewers) {
        return viewers;
    }
}
