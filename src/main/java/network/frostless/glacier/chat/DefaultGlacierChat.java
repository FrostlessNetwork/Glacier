package network.frostless.glacier.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import network.frostless.glacier.user.Users;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DefaultGlacierChat extends AbstractChat {

    @Override
    public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
        final GameUser user = Users.getUser(source.getUniqueId(), GameUser.class);

        return Component
                .space()
                .append(sourceDisplayName)
                .append(Component.space())
                .append(Component.text("[" + user.getGameIdentifier() + "]").color(TextColor.color(0xFFACB)))
                .append(Component.space())
                .append(message);

    }

    @Override
    public Set<Audience> filterViewers(Player source, Set<Audience> viewers) {

        final GameUser sourceUser = Users.getUser(source.getUniqueId(), GameUser.class);

        viewers.removeIf(viewer -> {
            final GameUser user = Users.getUser(((Player) viewer).getUniqueId(), GameUser.class);
            return !user.getGameIdentifier().equals(sourceUser.getGameIdentifier());
        });

        return viewers;
    }
}
