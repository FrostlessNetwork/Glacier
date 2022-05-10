package network.frostless.glacier.exceptions;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionAdapter;

public class GlacierExceptionAdapter extends BukkitExceptionAdapter {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public void invalidGameIdentifier(@NotNull BukkitCommandActor actor, @NotNull GameNotFoundException exception) {
        actor.reply(compose("<red>Could not find game '<gold>" + exception.getName() + "<red>'"));
    }


    private static String compose(@NotNull String message, @NotNull TagResolver... resolvers) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(mm.deserialize(message, resolvers));
    }
}
