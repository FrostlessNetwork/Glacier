package network.frostless.glacier.game.mechanics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import network.frostless.glacierapi.mechanics.GameMechanicHandler;
import network.frostless.glacierapi.user.GameUser;
import org.bukkit.entity.Player;

public class DefaultGameMechanicHandler<U extends GameUser> implements GameMechanicHandler<U> {

    private final MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void onDeath(GameUser user) {
        final Player player = user.getPlayer();
        spectate(user);
        user.getPlayer().spigot().respawn();
        player.teleport(user.getGame().getWorldCenter());


        user.getGame().executeUsers((u) -> {
            TagResolver tags = TagResolver.builder()
                    .tag("player", Tag.inserting(u.getDisplayName()))
                    .tag("remaining", Tag.inserting(Component.text(user.getGame().getIngamePlayers())))
                    .build();

            u.sendMessage(mm.deserialize("<red><player> <gray>died! There are <remaining> players left!", tags));
        });
    }

    @Override
    public void respawn(GameUser user) {
        user.getPlayer().setGameMode(org.bukkit.GameMode.SURVIVAL);
        user.getPlayer().teleport(user.getGame().getWorldCenter());
    }

    @Override
    public void spectate(GameUser user) {
        user.getPlayer().getInventory().clear();
        user.getPlayer().setGameMode(org.bukkit.GameMode.SPECTATOR);
    }
}
