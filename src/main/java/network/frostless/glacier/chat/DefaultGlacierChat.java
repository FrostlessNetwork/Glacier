package network.frostless.glacier.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import network.frostless.bukkitapi.ViewerFilter;
import network.frostless.glacier.Glacier;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class DefaultGlacierChat implements ChatRenderer, ViewerFilter {

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        final GameUser user = Users.getUser(source.getUniqueId(), GameUser.class);

        List<GameUser> gameUsers = Glacier.get().getGameManager().usersIn(user.getGameIdentifier());

        return Component.empty();
    }

    @Override
    public Set<Audience> filterViewers(Set<Audience> viewers) {

        return viewers;
    }
}
